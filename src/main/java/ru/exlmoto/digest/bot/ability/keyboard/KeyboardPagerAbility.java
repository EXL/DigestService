/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
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
		User user = callback.from();
		int messageId = message.messageId();

		int page = getPageFromArgument(Keyboard.chopKeyboardNameLast(callback.data()));

		if (handleQuery(callback.id(), user, page, sender, helper)) {
			handle(messageId, message.chat(), callback.from(), page, true, sender);
		}
	}

	protected boolean sendCallbackQueryPage(String callbackId, LocaleHelper locale, int page, BotSender sender) {
		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.pager.page") + " " + page);
		return true;
	}

	/*
	 * Intelligent pager from https://lab.exlmoto.ru/digests page ported for Telegram.
	 * Source: https://github.com/EXL/DigestBot/blob/master/Stuff/DigestHistorySite/index.php#L50
	 */
	public InlineKeyboardMarkup getMarkup(LocaleHelper locale, BotConfiguration config, int page, int totalPages) {
		if (totalPages <= 1) {
			return null;
		}

		int current = page;
		if (current > totalPages) {
			current = totalPages;
		}

		int paginPP = config.getDigestPageDeep();

		int start = current - ((paginPP / 2) + 1);
		if (start < 0) {
			start = 0;
		}

		int end = current + (paginPP / 2);
		if (end > totalPages) {
			end = totalPages;
		}

		List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
		if (start > 0) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.left.first"))
				.callbackData(callbackPageData(1)));
		}
		if (current > 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.left"))
				.callbackData(callbackPageData(current - 1)));
		}
		for (int i = start; i < end; i++) {
			keyboardRow.add(new InlineKeyboardButton(
				(i == current - 1) ?
					"|" + (i + 1) + "|" :
					String.valueOf(i + 1)
			).callbackData(callbackPageData(i + 1)));
		}
		if (current < totalPages) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.pager.right"))
				.callbackData(callbackPageData(current + 1)));
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

	protected int getPageFromArgument(String key) {
		int page = 1;
		try {
			page = NumberUtils.parseNumber(key, Integer.class);
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot parse inline page key: '%s' as Integer.", key), nfe);
		}
		return Math.max(page, 1);
	}

	protected abstract Keyboard getKeyboard();

	protected abstract boolean handleQuery(String callbackId, User user, int page, BotSender sender, BotHelper helper);

	public abstract void handle(int messageId, Chat chat, User user, int page, boolean edit, BotSender sender);
}
