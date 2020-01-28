package ru.exlmoto.digest.bot.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import javax.annotation.PostConstruct;

@Component
public class SubscribeKeyboard extends BotKeyboard {
	private final LocalizationHelper locale;

	private InlineKeyboardMarkup markup = null;

	public SubscribeKeyboard(LocalizationHelper locale) {
		this.locale = locale;
	}

	private enum Subscription {
		digest_subscribe,
		digest_unsubscribe,
		motofan_subscribe,
		motofan_unsubscribe
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

	}
}
