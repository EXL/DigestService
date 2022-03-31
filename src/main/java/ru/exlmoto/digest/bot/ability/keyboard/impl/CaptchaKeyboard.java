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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.util.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.task.CaptchaTask;
import ru.exlmoto.digest.bot.task.data.CaptchaData;
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
	private final Logger log = LoggerFactory.getLogger(CaptchaKeyboard.class);

	private final BotSender sender;
	private final LocaleHelper locale;
	private final ThreadPoolTaskScheduler scheduler;

	private final Map<String, CaptchaData> captchaChecksMap = new HashMap<>();

	private enum Button {
		C650,
		E398,
		V500
	}

	public CaptchaKeyboard(BotSender sender, LocaleHelper locale, ThreadPoolTaskScheduler scheduler) {
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
		int joinedMessageId = message.messageId();

		// 1. Restrict user rights.
		log.info(String.format("==> Restrict user with id '%d' in the '%d' chat.", chatId, userId));
		sender.restrictUserInChat(chatId, userId);

		// 2. Send CAPTCHA message with buttons and fill HashMap.
		Answer<String> res = sender.replyMarkdown(chatId, joinedMessageId,
			locale.i18n("bot.captcha.question"), getMarkup());

		if (res.ok()) {
			String key = generateKey(chatId, userId, res.answer());
			Pair<Long, Long> messageIds = Pair.of((long) joinedMessageId, 0L);

			// 3. Create timer with a key.
			final int delayMs = 10000;
			log.info(String.format("==> Schedule CAPTHA deletion and ban task for '%d' sec.", delayMs / 100));
			ScheduledFuture<?> timerHandle =
				scheduler.schedule(new CaptchaTask(this, key, messageIds),
					new Date(System.currentTimeMillis() + delayMs));

			// 4. Put data and timer handle to a HashMap.
			captchaChecksMap.put(key, new CaptchaData(messageIds, timerHandle));
		}
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();
		long userId = callback.from().id();
		int messageId = message.messageId();
		String keyButton = Keyboard.chopKeyboardNameLeft(callback.data());
		String keyCaptcha = generateKey(chatId, userId, String.valueOf(messageId));

		log.debug(String.format("captchaChecksMap size: '%d'.", captchaChecksMap.size()));

		if (captchaChecksMap.containsKey(keyCaptcha)) {
			CaptchaData data = captchaChecksMap.get(keyCaptcha);
			int joinMessageId = Math.toIntExact(data.getMessageIds().getFirst());
			if (keyButton.equals(Button.E398.name())) {
				log.info(String.format("==> Ok CAPTCHA User: '%s'.", helper.getValidUsername(callback.from())));
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.solved"));

				processCorrectAnswer(chatId, userId, messageId);

				sender.replySimple(chatId, joinMessageId,
					locale.i18nRU("bot.event.user.new", helper.getValidUsername(callback.from())));
			} else {
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.failed"));
				log.info(String.format("==> Fail CAPTCHA User: '%s'.", helper.getValidUsername(callback.from())));

				processWrongAnswer(chatId, userId, messageId, joinMessageId);
			}
			log.info("==> Cancel CAPTCHA timer and remove key from HashMap.");
			data.getTimerHandle().cancel(true);
			captchaChecksMap.remove(keyCaptcha);
		} else {
			log.info(String.format("==> Wrong CAPTCHA User: '%s'.", helper.getValidUsername(callback.from())));
			sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.wrong"));
		}

		log.debug(String.format("captchaChecksMap size: '%d'.", captchaChecksMap.size()));
	}

	private void processCorrectAnswer(long chatId, long userId, int messageId) {
		sender.allowUserInChat(chatId, userId);
		sender.deleteMessageInChat(chatId, messageId);
	}

	public void processWrongAnswer(long chatId, long userId, int messageId, int joinMessageId) {
		sender.banUserInChat(chatId, userId, FilterHelper.getCurrentUnixTime() + 86400); // 1 day.
		sender.deleteMessageInChat(chatId, joinMessageId);
		sender.deleteMessageInChat(chatId, messageId);
	}

	private String generateKey(long chatId, long userId, String captchaMessageId) {
		return String.format("%d|%d|", chatId, userId) + captchaMessageId;
	}
}
