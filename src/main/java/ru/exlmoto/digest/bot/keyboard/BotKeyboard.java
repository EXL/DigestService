package ru.exlmoto.digest.bot.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

public abstract class BotKeyboard {
	public static final String DELIMITER = "_";
	public static final String RATE = "rate" + DELIMITER;
	public static final String CHART = "chart" + DELIMITER;
	public static final String SUBSCRIBE = "subscribe" + DELIMITER;

	public void process(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		new Thread(() -> handle(helper, sender, locale, callback)).start();
	}

	public abstract InlineKeyboardMarkup getMarkup();

	protected abstract void handle(BotHelper helper, BotSender sender,
	                               LocalizationHelper locale, CallbackQuery callback);
}
