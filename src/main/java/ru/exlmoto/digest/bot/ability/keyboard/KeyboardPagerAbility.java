package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.NumberUtils;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyboardPagerAbility extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(KeyboardPagerAbility.class);

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		int messageId = message.messageId();
		long chatId = message.chat().id();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLast(callback.data());

		int page = 1;
		try {
			page = NumberUtils.parseNumber(key, Integer.class);
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot parse inline page key: '%s' as Integer.", key), nfe);
		}

		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.pager.page") + " " + page);

		handle(chatId, messageId, callback.from(), page, true, sender);
	}

	/*
	 * Intelligent pager from https://lab.exlmoto.ru/digests page ported for Telegram.
	 * Source: https://github.com/EXL/DigestBot/blob/master/Stuff/DigestHistorySite/index.php#L50
	 */
	public InlineKeyboardMarkup getMarkup(LocaleHelper locale, BotConfiguration config, int page, int totalPages) {
		int paginPP = config.getDigestPageDeep();
		if (totalPages <= 1) {
			return null;
		}

		int start = page - ((paginPP / 2) + 1);
		if (start < 0) {
			start = 0;
		}

		int end = page + (paginPP / 2);
		if (end > totalPages) {
			end = totalPages;
		}

		List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
		if (start > 0) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.left.first"))
				.callbackData(callbackPageData(1)));
		}
		if (page > 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.left"))
				.callbackData(callbackPageData(page - 1)));
		}
		for (int i = start; i < end; i++) {
			keyboardRow.add(new InlineKeyboardButton(
				(i == page - 1) ?
					"|" + (i + 1) + "|" :
					String.valueOf(i + 1)
			).callbackData(callbackPageData(i + 1)));
		}
		if (page < totalPages) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.right"))
				.callbackData(callbackPageData(page + 1)));
		}
		if (end < totalPages) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.right.last"))
				.callbackData(callbackPageData(totalPages)));
		}
		return new InlineKeyboardMarkup((InlineKeyboardButton[]) ArrayUtils.toArray(keyboardRow));
	}

	protected String callbackPageData(int page) {
		return getKeyboard().withName() + Keyboard.PAGE + page;
	}

	protected abstract Keyboard getKeyboard();

	public abstract void handle(long chatId, int messageId, User user, int page, boolean edit, BotSender sender);
}
