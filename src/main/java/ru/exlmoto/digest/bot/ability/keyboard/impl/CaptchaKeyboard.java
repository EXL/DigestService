/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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
import com.pengrad.telegrambot.request.BanChatMember;
import com.pengrad.telegrambot.request.RestrictChatMember;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class CaptchaKeyboard extends KeyboardSimpleAbility {
	private final BotTelegram telegram;
	private final BotSender sender;
	private final LocaleHelper locale;

	private enum Button {
		C650,
		E398,
		V500
	}

	public CaptchaKeyboard(BotTelegram telegram, BotSender sender, LocaleHelper locale) {
		this.telegram = telegram;
		this.sender = sender;
		this.locale = locale;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return new InlineKeyboardMarkup(
			new InlineKeyboardButton(locale.i18n("bot.captcha.button1"))
				.callbackData(Keyboard.captcha.withName() + Button.C650),
			new InlineKeyboardButton(locale.i18n("bot.captcha.button2"))
				.callbackData(Keyboard.captcha.withName() + Button.E398),
			new InlineKeyboardButton(locale.i18n("bot.captcha.button3"))
				.callbackData(Keyboard.captcha.withName() + Button.V500)
		);
	}

	public void processCaptchaForUser(long chatId, Message message) {
		// First step: restrict user.
		RestrictChatMember restrictChatMember = new RestrictChatMember(chatId, message.from().id());
		telegram.getBot().execute(restrictChatMember);

		sender.replyMarkdown(chatId, message.messageId(), locale.i18n("bot.captcha.question"), getMarkup());
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();
		int messageId = message.messageId();

		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		if (key.equals(Button.E398.name())) {
			sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.solved"));

			RestrictChatMember restrictChatMember = new RestrictChatMember(chatId, callback.from().id())
				.canSendMessages(true).canSendMediaMessages(true).canAddWebPagePreviews(true).canSendOtherMessages(true);
			telegram.getBot().execute(restrictChatMember);

			// Reply.
		} else {
			sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.failed"));

			BanChatMember banChatMember = new BanChatMember(chatId, callback.from().id())
				.untilDate((int) FilterHelper.getCurrentUnixTime() + 86400); // 1 day.
			telegram.getBot().execute(banChatMember);
		}
//		sender.editMarkdown(chatId, messageId, service.markdownReport(key), markup);
	}
}
