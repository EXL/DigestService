/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class GreetingKeyboard extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(GreetingKeyboard.class);

	private final LocaleHelper locale;
	private final DatabaseService service;

	private enum Greeting {
		off,
		on
	}

	public GreetingKeyboard(LocaleHelper locale, DatabaseService service) {
		this.locale = locale;
		this.service = service;
	}

	public InlineKeyboardMarkup getMarkup(boolean status) {
		return (status) ?
			new InlineKeyboardMarkup(
				new InlineKeyboardButton(locale.i18n("bot.command.greeting.button.on"))
					.callbackData(Keyboard.greeting.withName() + Greeting.on)) :
			new InlineKeyboardMarkup(
				new InlineKeyboardButton(locale.i18n("bot.command.greeting.button.off"))
					.callbackData(Keyboard.greeting.withName() + Greeting.off));
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		if (sender.isUserChatAdministrator(chatId, callback.from().id())) {
			handleGreeting(chatId, message.messageId(), sender, callbackId, checkGreeting(key));
		} else {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.show.admin"));
		}
	}

	private void handleGreeting(long chatId, int messageId, BotSender sender, String callbackId, Greeting greeting) {
		try {
			switch (greeting) {
				case off: {
					disableGreetings(chatId, messageId, callbackId, sender);
					break;
				}
				case on: {
					enableGreetings(chatId, messageId, callbackId, sender);
					break;
				}
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save or delete greeting object from database.", dae);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.database"));
		}
	}

	private Greeting checkGreeting(String key) {
		try {
			return Greeting.valueOf(key);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong greeting key: '%s', return default '%s'.", key, Greeting.off), iae);
		}
		return Greeting.off;
	}

	private void disableGreetings(long chatId, int messageId, String callbackId, BotSender sender) {
		service.addChatToGreetingIgnores(chatId);
		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.greeting.off"));
		processGreetingStatusMessage(chatId, messageId, true, sender);
	}

	private void enableGreetings(long chatId, int messageId, String callbackId, BotSender sender) {
		service.deleteChatFromGreetingIgnores(chatId);
		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.greeting.on"));
		processGreetingStatusMessage(chatId, messageId, true, sender);
	}

	public void processGreetingStatusMessage(long chatId, int messageId, boolean edit, BotSender sender) {
		try {
			boolean status = service.checkGreeting(chatId);
			processMessageAux(chatId, messageId,
				String.format(locale.i18n("bot.command.greeting"), getGreetingStatus(status)),
				getMarkup(!status), edit, sender);
		} catch (DataAccessException dae) {
			log.error("Cannot get greeting object from database.", dae);
			processMessageAux(chatId, messageId,
				String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage()),
				null, edit, sender);
		}
	}

	private void processMessageAux(long chatId,
	                               int messageId,
	                               String answer,
	                               InlineKeyboardMarkup markup,
	                               boolean edit,
	                               BotSender sender) {
		if (edit) {
			sender.editMarkdown(chatId, messageId, answer, markup);
		} else {
			sender.replyMarkdown(chatId, messageId, answer, markup);
		}
	}

	private String getGreetingStatus(boolean status) {
		return (status) ?
			locale.i18n("bot.command.greeting.status.on") :
			locale.i18n("bot.command.greeting.status.off");
	}
}
