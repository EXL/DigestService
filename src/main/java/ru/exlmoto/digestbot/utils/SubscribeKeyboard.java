package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.entities.MotoFanSubscriberEntity;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class SubscribeKeyboard {
	private final YamlLocalizationHelper mYamlLocalizationHelper;

	private final String[] mAdministrators;

	@Autowired
	public SubscribeKeyboard(final YamlLocalizationHelper aYamlLocalizationHelper,
	                         @Value("${digestbot.admins}") final String[] aAdministrators) {
		mYamlLocalizationHelper = aYamlLocalizationHelper;
		mAdministrators = aAdministrators;
	}

	public InlineKeyboardMarkup getSubscribeKeyboard() {
		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		lKeyboardRow.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
			"command.subscribe.button.subscribe")).setCallbackData("subscribe.subscribe"));
		lKeyboardRow.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
			"command.subscribe.button.unsubscribe")).setCallbackData("subscribe.unsubscribe"));

		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	private boolean checkChatIdInSubscribesDataBase(final DigestBot aDigestBot, final Long aChatId) {
		return aDigestBot.getIMotoFanSubscribersRepository()
			.findOneMotoFanSubscriberEntityBySubscription(aChatId) != null;
	}

	public void handleSubscribeKeyboard(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		final Message lMessage = aCallbackQuery.getMessage();
		final boolean subscribe = !aCallbackQuery.getData().contains("unsubscribe");
		if (!lMessage.getChat().isUserChat()) {
			if (ArrayUtils.contains(mAdministrators, aCallbackQuery.getFrom().getUserName())) {
				handleSubscription(aDigestBot, aCallbackQuery, lMessage, subscribe);
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.subscribe.admin"));
			}
		} else {
			handleSubscription(aDigestBot, aCallbackQuery, lMessage, subscribe);
		}
	}

	private void handleSubscription(final DigestBot aDigestBot,
	                                final CallbackQuery aCallbackQuery,
	                                final Message lMessage,
	                                final boolean subscribe) {
		final boolean lChatIdInDataBase = checkChatIdInSubscribesDataBase(aDigestBot, lMessage.getChatId());
		if (subscribe) {
			if (lChatIdInDataBase) {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.subscribe.exist"));
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.subscribe.subscribed"));
				final MotoFanSubscriberEntity lMotoFanSubscriberEntity = new MotoFanSubscriberEntity();
				lMotoFanSubscriberEntity.setSubscription(lMessage.getChatId());
				aDigestBot.getIMotoFanSubscribersRepository().save(lMotoFanSubscriberEntity);
			}
		} else {
			if (lChatIdInDataBase) {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.subscribe.unsubscribed"));
				aDigestBot.getIMotoFanSubscribersRepository()
					.deleteMotoFanSubscriberEntityBySubscription(lMessage.getChatId());
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.unsubscribe.exist"));
			}
		}
		editSubscriptionMessage(aDigestBot, lMessage.getChatId(), lMessage.getMessageId());
	}

	private void editSubscriptionMessage(final DigestBot aDigestBot,
	                                     final Long aChatId,
	                                     final Integer aMessageId) {
		final boolean lChatInDataBase = aDigestBot.getIMotoFanSubscribersRepository()
			                                .findOneMotoFanSubscriberEntityBySubscription(aChatId) != null;
		final String lStatus = (lChatInDataBase) ?
			                       mYamlLocalizationHelper.getLocalizedString("command.subscribe.subscribed") :
			                       mYamlLocalizationHelper.getLocalizedString("command.subscribe.unsubscribed");
		aDigestBot.editMarkdownMessageWithKeyboard(aChatId, aMessageId,
			String.format(mYamlLocalizationHelper.getLocalizedString("command.subscribe"), aChatId, lStatus),
			aDigestBot.getSubscribeKeyboard().getSubscribeKeyboard());
	}
}
