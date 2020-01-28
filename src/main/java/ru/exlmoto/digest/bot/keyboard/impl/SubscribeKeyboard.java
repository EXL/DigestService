package ru.exlmoto.digest.bot.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.repository.SubDigestRepository;
import ru.exlmoto.digest.repository.SubMotofanRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import javax.annotation.PostConstruct;

@Component
public class SubscribeKeyboard extends BotKeyboard {
	private final Logger log = LoggerFactory.getLogger(SubscribeKeyboard.class);

	private enum Subscription {
		digest_subscribe,
		digest_unsubscribe,
		motofan_subscribe,
		motofan_unsubscribe
	}

	private final LocalizationHelper locale;
	private final SubDigestRepository digestRepository;
	private final SubMotofanRepository motofanRepository;
	private final BotTelegram telegram;
	private final BotHelper helper;

	private InlineKeyboardMarkup markup = null;

	public SubscribeKeyboard(SubDigestRepository digestRepository,
	                         SubMotofanRepository motofanRepository,
	                         BotTelegram telegram,
	                         BotHelper helper,
	                         LocalizationHelper locale) {
		this.digestRepository = digestRepository;
		this.motofanRepository = motofanRepository;
		this.telegram = telegram;
		this.helper = helper;
		this.locale = locale;
	}

	@PostConstruct
	private void generateKeyboard() {
		markup = new InlineKeyboardMarkup(
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.motofan"))
					.callbackData(SUBSCRIBE + Subscription.motofan_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.motofan"))
					.callbackData(SUBSCRIBE + Subscription.motofan_unsubscribe)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.digest"))
					.callbackData(SUBSCRIBE + Subscription.digest_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.digest"))
					.callbackData(SUBSCRIBE + Subscription.digest_unsubscribe)
			}
		);
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return markup;
	}

	@Override
	protected void handle(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		log.warn("Chop: " + callback.data());
	}

	public String getSubscribeStatusMessage(Chat chat) {
		long chatId = chat.id();
		String chatTitle = helper.getValidChatName(chat, telegram.getFirstName());
		try {
			return String.format(locale.i18n("bot.command.subscribe"), chatTitle, chatId,
				getSubscribeStatus(motofanRepository.findSubMotofanEntityBySubscription(chatId) != null),
				getSubscribeStatus(digestRepository.findSubDigestEntityBySubscription(chatId) != null));
		} catch (DataAccessException dae) {
			log.error("Cannot get object from database.", dae);
			return String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
	}

	private String getSubscribeStatus(boolean status) {
		return (status) ?
			locale.i18n("bot.command.subscribe.subscribed") :
			locale.i18n("bot.command.subscribe.unsubscribed");
	}
}
