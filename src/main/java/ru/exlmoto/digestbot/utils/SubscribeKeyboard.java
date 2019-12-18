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
import ru.exlmoto.digestbot.entities.DigestSubscriberEntity;
import ru.exlmoto.digestbot.entities.MotoFanSubscriberEntity;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional // TODO: ???
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

		List<InlineKeyboardButton> lKeyboardRowFirst = new ArrayList<>();
		lKeyboardRowFirst.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
			"command.subscribe.button.subscribe.motofan")).setCallbackData("subscribe.subscribe.motofan"));
		lKeyboardRowFirst.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
			"command.subscribe.button.unsubscribe.motofan")).setCallbackData("subscribe.unsubscribe.motofan"));

		List<InlineKeyboardButton> lKeyboardRowSecond = new ArrayList<>();
		lKeyboardRowSecond.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
				"command.subscribe.button.subscribe.digest")).setCallbackData("subscribe.subscribe.digest"));
		lKeyboardRowSecond.add(new InlineKeyboardButton(mYamlLocalizationHelper.getLocalizedString(
				"command.subscribe.button.unsubscribe.digest")).setCallbackData("subscribe.unsubscribe.digest"));

		lKeyboardMarkup.add(lKeyboardRowFirst);
		lKeyboardMarkup.add(lKeyboardRowSecond);

		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	private boolean checkChatIdInSubscribesDataBase(final DigestBot aDigestBot,
													final Long aChatId,
													final boolean aIsMotoFan) {
		return (aIsMotoFan) ?
				aDigestBot.getIMotoFanSubscribersRepository().findOneMotoFanSubscriberEntityBySubscription(aChatId)
						!= null :
				aDigestBot.getIDigestSubscribersRepository().findOneDigestSubscriberEntityBySubscription(aChatId)
						!= null;
	}

	public void handleSubscribeKeyboard(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		final Message lMessage = aCallbackQuery.getMessage();
		final boolean lIsSubscribe = !aCallbackQuery.getData().contains("unsubscribe");
		final boolean lIsMotoFan = aCallbackQuery.getData().contains("motofan");
		if (!lMessage.getChat().isUserChat()) {
			if (ArrayUtils.contains(mAdministrators, aCallbackQuery.getFrom().getUserName())) {
				handleSubscription(aDigestBot, aCallbackQuery, lMessage, lIsSubscribe, lIsMotoFan);
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.subscribe.admin"));
			}
		} else {
			handleSubscription(aDigestBot, aCallbackQuery, lMessage, lIsSubscribe, lIsMotoFan);
		}
	}

	private void handleSubscription(final DigestBot aDigestBot,
	                                final CallbackQuery aCallbackQuery,
	                                final Message aMessage,
	                                final boolean aIsSubscribe,
	                                final boolean aMotoFan) {
		final boolean lChatIdInDataBase = checkChatIdInSubscribesDataBase(aDigestBot, aMessage.getChatId(), aMotoFan);
		if (aIsSubscribe) {
			if (lChatIdInDataBase) {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.subscribe.exist"));
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.subscribe.subscribed"));
				if (aMotoFan) {
					final MotoFanSubscriberEntity lMotoFanSubscriberEntity = new MotoFanSubscriberEntity();
					lMotoFanSubscriberEntity.setSubscription(aMessage.getChatId());
					aDigestBot.getIMotoFanSubscribersRepository().save(lMotoFanSubscriberEntity);
				} else {
					final DigestSubscriberEntity lDigestSubscriberEntity = new DigestSubscriberEntity();
					lDigestSubscriberEntity.setSubscription(aMessage.getChatId());
					aDigestBot.getIDigestSubscribersRepository().save(lDigestSubscriberEntity);
				}
			}
		} else {
			if (lChatIdInDataBase) {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.subscribe.unsubscribed"));
				if (aMotoFan) {
					aDigestBot.getIMotoFanSubscribersRepository()
							.deleteMotoFanSubscriberEntityBySubscription(aMessage.getChatId());
				} else {
					aDigestBot.getIDigestSubscribersRepository()
							.deleteDigestSubscriberEntityBySubscription(aMessage.getChatId());
				}
			} else {
				aDigestBot.createAndSendAnswerCallbackQuery(aCallbackQuery.getId(),
					mYamlLocalizationHelper.getLocalizedString("inline.error.unsubscribe.exist"));
			}
		}
		editSubscriptionMessage(aDigestBot, aMessage.getChatId(), aMessage.getMessageId());
	}

	private void editSubscriptionMessage(final DigestBot aDigestBot,
	                                     final Long aChatId,
	                                     final Integer aMessageId) {
		final String lStatusMotofan = getStatusStringAux(aDigestBot.getIMotoFanSubscribersRepository()
				.findOneMotoFanSubscriberEntityBySubscription(aChatId) != null);
		final String lStatusDigest = getStatusStringAux(aDigestBot.getIDigestSubscribersRepository()
				.findOneDigestSubscriberEntityBySubscription(aChatId) != null);

		aDigestBot.editMarkdownMessageWithKeyboard(aChatId, aMessageId,
			String.format(mYamlLocalizationHelper.getLocalizedString("command.subscribe"),
					aChatId, lStatusMotofan, lStatusDigest),
			aDigestBot.getSubscribeKeyboard().getSubscribeKeyboard());
	}

	private String getStatusStringAux(final boolean aStatus) {
		return (aStatus) ?
				mYamlLocalizationHelper.getLocalizedString("command.subscribe.subscribed") :
				mYamlLocalizationHelper.getLocalizedString("command.subscribe.unsubscribed");
	}
}
