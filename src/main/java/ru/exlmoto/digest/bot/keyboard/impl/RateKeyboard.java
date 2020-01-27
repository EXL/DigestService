package ru.exlmoto.digest.bot.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class RateKeyboard extends BotKeyboard {
	private final ExchangeService service;

	public RateKeyboard(ExchangeService service) {
		this.service = service;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		InlineKeyboardButton[] keyboardRow = new InlineKeyboardButton[ExchangeKey.values().length];
		int i = 0;
		for(ExchangeKey key: ExchangeKey.values()) {
			keyboardRow[i++] = new InlineKeyboardButton(service.buttonLabel(key.name())).callbackData(RATE + key);
		}
		return new InlineKeyboardMarkup(keyboardRow);
	}

	@Override
	protected void handle(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		String key = callback.data().replaceAll(RATE, "");
		/*
		String title;
		try {
			title = ExchangeKey.valueOf()
		}
		*/

		sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.selected") + " ");
	}
}
