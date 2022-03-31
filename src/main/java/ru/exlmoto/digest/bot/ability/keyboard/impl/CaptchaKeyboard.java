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
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.RestrictChatMember;

import org.springframework.data.util.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.task.CaptchaTask;
import ru.exlmoto.digest.bot.task.data.CaptchaData;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class CaptchaKeyboard extends KeyboardSimpleAbility {
	private final BotTelegram telegram;
	private final BotSender sender;
	private final LocaleHelper locale;
	private final ThreadPoolTaskScheduler scheduler;

	private final Map<String, CaptchaData> captchaChecksMap = new HashMap<>();

	private enum Button {
		C650,
		E398,
		V500
	}

	public CaptchaKeyboard(BotTelegram telegram, BotSender sender, LocaleHelper locale,
	                       ThreadPoolTaskScheduler scheduler) {
		this.telegram = telegram;
		this.sender = sender;
		this.locale = locale;
		this.scheduler = scheduler;
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
		long userId = message.from().id();
		long joinedMessageId = message.messageId();

		// 1. Restrict user rights.
		RestrictChatMember restrictChatMember = new RestrictChatMember(chatId, userId);
		telegram.getBot().execute(restrictChatMember);

		// 2. Send CAPTCHA message with buttons and fill HashMap.
		Answer<String> res =
			sender.replyMarkdown(chatId, message.messageId(), locale.i18n("bot.captcha.question"), getMarkup());
		if (res.ok()) {
			String key = generateKey(chatId, userId, res.answer());
			Pair<Long, Long> messageIds = Pair.of(joinedMessageId, 0L);

			// 3. Create timer with a key.
			ScheduledFuture<?> timerHandle =
				scheduler.schedule(new CaptchaTask(this, key, messageIds),
					new Date(System.currentTimeMillis() + 10000));

			// 4. Put data to HashMap.
			captchaChecksMap.put(key, new CaptchaData(messageIds, timerHandle));
		} else {
			// TODO: Log error?!
		}
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		System.out.println("SIZE: " + captchaChecksMap.size());
		Message message = callback.message();
		long chatId = message.chat().id();
		long userId = callback.from().id();
		int messageId = message.messageId();
		String keyButton = Keyboard.chopKeyboardNameLeft(callback.data());
		String keyCaptcha = generateKey(chatId, userId, String.valueOf(messageId));

		if (captchaChecksMap.containsKey(keyCaptcha)) {
			CaptchaData data = captchaChecksMap.get(keyCaptcha);
			int joinMessageId = Math.toIntExact(data.getMessageIds().getFirst());
			if (keyButton.equals(Button.E398.name())) {
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.solved"));

				processCorrectAnswer(chatId, userId, messageId);

				sender.replySimple(chatId, joinMessageId,
					locale.i18nRU("bot.event.user.new", helper.getValidUsername(callback.from())));
			} else {
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.failed"));

				processWrongAnswer(chatId, userId, messageId, joinMessageId);
			}
			data.getTimerHandle().cancel(true);
			captchaChecksMap.remove(keyCaptcha);
		} else {
			sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.wrong"));
		}
		System.out.println("SIZE: " + captchaChecksMap.size());
	}

	private void processCorrectAnswer(long chatId, long userId, int messageId) {
		RestrictChatMember restrictChatMember = new RestrictChatMember(chatId, userId)
			.canSendMessages(true)
			.canSendMediaMessages(true)
			.canAddWebPagePreviews(true)
			.canSendOtherMessages(true);
		telegram.getBot().execute(restrictChatMember);

		DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
		telegram.getBot().execute(deleteMessage);
	}

	public void processWrongAnswer(long chatId, long userId, int messageId, int joinMessageId) {
		BanChatMember banChatMember = new BanChatMember(chatId, userId)
			.untilDate(Math.toIntExact(FilterHelper.getCurrentUnixTime() + 86400)); // 1 day.
		telegram.getBot().execute(banChatMember);

		DeleteMessage deleteMessageJoin = new DeleteMessage(chatId, joinMessageId);
		telegram.getBot().execute(deleteMessageJoin);

		DeleteMessage deleteMessageCaptcha = new DeleteMessage(chatId, messageId);
		telegram.getBot().execute(deleteMessageCaptcha);
	}

	private String generateKey(long chatId, long userId, String captchaMessageId) {
		return String.format("%d|%d|", chatId, userId) + captchaMessageId;
	}
}
