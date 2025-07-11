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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.task.CaptchaTask;
import ru.exlmoto.digest.bot.task.data.CaptchaData;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CaptchaKeyboard extends KeyboardSimpleAbility {
	private final Logger log = LoggerFactory.getLogger(CaptchaKeyboard.class);

	private final BotConfiguration config;
	private final BotSender sender;
	private final BotHelper botHelper;
	private final LocaleHelper locale;
	private final ResourceHelper resource;
	private final ThreadPoolTaskScheduler scheduler;

	private final Map<String, CaptchaData> captchaChecksMap = new HashMap<>();

	private enum ChatFlavor {
		MotoFan,
		SiePatch
	}

	@Value("${bot.captcha-chat-id-motofan}")
	private long captchaChatIdMotofan;

	@Value("${bot.captcha-chat-id-siepatchdb}")
	private long captchaChatIdSiepatchdb;

	@Value("classpath:bot/captcha/Motorola_E398_Original_Photo_1.jpg")
	private Resource captchaPhotoMotoFan_1;

	@Value("classpath:bot/captcha/Motorola_E398_Original_Photo_2.jpg")
	private Resource captchaPhotoMotoFan_2;

	@Value("classpath:bot/captcha/Siemens_S75_Original_Photo_1.jpg")
	private Resource captchaPhotoSiePatch_1;

	@Value("classpath:bot/captcha/Siemens_S75_Original_Photo_2.jpg")
	private Resource captchaPhotoSiePatch_2;

	private enum ButtonMotoFan {
		C650,
		E398,
		V500
	}

	private enum ButtonSiePatch {
		C60,
		S75,
		SX1
	}

	public CaptchaKeyboard(BotConfiguration config, BotSender sender,
	                       BotHelper botHelper, LocaleHelper locale, ResourceHelper resource,
	                       ThreadPoolTaskScheduler scheduler) {
		this.config = config;
		this.sender = sender;
		this.botHelper = botHelper;
		this.locale = locale;
		this.resource = resource;
		this.scheduler = scheduler;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return getMarkupChat(ChatFlavor.MotoFan);
	}

	public InlineKeyboardMarkup getMarkupChat(ChatFlavor chatFlavor) {
		String button1;
		String button2;
		String button3;

		switch (chatFlavor) {
			default:
			case MotoFan:
				button1 = ButtonMotoFan.C650.name();
				button2 = ButtonMotoFan.E398.name();
				button3 = ButtonMotoFan.V500.name();
				break;
			case SiePatch:
				button1 = ButtonSiePatch.C60.name();
				button2 = ButtonSiePatch.S75.name();
				button3 = ButtonSiePatch.SX1.name();
				break;
		}

		return new InlineKeyboardMarkup(
			new InlineKeyboardButton(button1).callbackData(Keyboard.captcha.withName() + button1),
			new InlineKeyboardButton(button2).callbackData(Keyboard.captcha.withName() + button2),
			new InlineKeyboardButton(button3).callbackData(Keyboard.captcha.withName() + button3)
		);
	}

	public Resource getResourceImg(ChatFlavor chatFlavor) {
		switch (chatFlavor) {
			default:
			case MotoFan:
				switch (ThreadLocalRandom.current().nextInt(0, 2)) {
					default:
					case 0:
						return captchaPhotoMotoFan_1;
					case 1:
						return captchaPhotoMotoFan_2;
				}
			case SiePatch:
				switch (ThreadLocalRandom.current().nextInt(0, 2)) {
					default:
					case 0:
						return captchaPhotoSiePatch_1;
					case 1:
						return captchaPhotoSiePatch_2;
				}
		}
	}

	public String getCorrectAnswer(ChatFlavor chatFlavor) {
		switch (chatFlavor) {
			default:
			case MotoFan:
				return ButtonMotoFan.E398.name();
			case SiePatch:
				return ButtonSiePatch.S75.name();
		}
	}

	public ChatFlavor getChatFlavor(long chatId) {
		if (chatId == captchaChatIdMotofan) {
			return ChatFlavor.MotoFan;
		} else if (chatId == captchaChatIdSiepatchdb) {
			return ChatFlavor.SiePatch;
		} else {
			log.warn(String.format("Unknown Chat flavor id: '%d'.", chatId));
			return ChatFlavor.MotoFan;
		}
	}

	public String getAdminUsername(ChatFlavor chatFlavor) {
		switch (chatFlavor) {
			default:
			case MotoFan:
				return "@exlmoto";
			case SiePatch:
				return "@Nanak0n";
		}
	}

	public void processCaptchaForUser(long chatId, Message message) {
		ChatFlavor chatFlavor = getChatFlavor(chatId);
		long userId = message.from().id();
		int joinedMessageId = message.messageId();
		int delay = config.getCaptchaDelay();

		// 1. Restrict user rights.
		log.info(String.format("==> Restrict user with id '%d' in the '%d' chat.", userId, chatId));
		sender.restrictUserInChat(chatId, userId);

		// 2. Send CAPTCHA message reply with image and buttons.
		Answer<String> res = sender.replyResourcePhotoToChat(
			chatId,
			resource.asByteArray(getResourceImg(chatFlavor)),
			joinedMessageId,
			String.format(
				locale.i18nU("bot.captcha.question", getAdminUsername(chatFlavor)),
				botHelper.getValidUsername(message.from()), delay
			),
			getMarkupChat(chatFlavor)
		);

		if (res.ok()) {
			String key = generateKey(chatId, userId, res.answer());

			// 3. Create timer with a key.
			log.info(String.format("==> Schedule CAPTCHA deletion and ban task for '%d' sec, key '%s'.", delay, key));
			ScheduledFuture<?> timerHandle =
				scheduler.schedule(new CaptchaTask(this, key, joinedMessageId),
					new Date(System.currentTimeMillis() + (delay * 1000L)));

			// 4. Put data and timer handle to a HashMap.
			captchaChecksMap.put(key, new CaptchaData(joinedMessageId, timerHandle));
		}
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();
		ChatFlavor chatFlavor = getChatFlavor(chatId);
		long userId = callback.from().id();
		int messageId = message.messageId();
		String keyButton = Keyboard.chopKeyboardNameLeft(callback.data());
		String keyCaptcha = generateKey(chatId, userId, String.valueOf(messageId));

		log.debug(String.format("captchaChecksMap size: '%d'.", captchaChecksMap.size()));

		if (captchaChecksMap.containsKey(keyCaptcha)) {
			CaptchaData data = captchaChecksMap.get(keyCaptcha);
			data.getTimerHandle().cancel(true);

			int joinMessageId = data.getJoinedMessageId();
			if (keyButton.equals(getCorrectAnswer(chatFlavor))) {
				log.info(String.format("==> Ok CAPTCHA User: '%s', answer: '%s'.",
					helper.getValidUsername(callback.from()), keyButton));
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.solved"));

				processCorrectAnswer(chatId, userId, messageId);

				sender.replySimple(chatId, joinMessageId,
					locale.i18nRU("bot.event.user.new", helper.getValidUsername(callback.from())));
			} else {
				sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.failed"));
				log.info(String.format("==> Fail CAPTCHA User: '%s', answer: '%s'.",
					helper.getValidUsername(callback.from()), keyButton));

				processWrongAnswer(chatId, userId, messageId, joinMessageId);
			}
			cleanCaptchaChecksMap(keyCaptcha);
		} else {
			log.info(String.format("==> Wrong CAPTCHA User: '%s' with '%d' id, answer: '%s'.",
				helper.getValidUsername(callback.from()), userId, keyButton));
			sender.sendCallbackQueryAnswer(callback.id(), locale.i18n("bot.inline.captcha.wrong"));
		}

		log.debug(String.format("captchaChecksMap size: '%d'.", captchaChecksMap.size()));
	}

	private void processCorrectAnswer(long chatId, long userId, int messageId) {
		sender.allowUserInChat(chatId, userId);
		sender.deleteMessageInChat(chatId, messageId);
	}

	public void processWrongAnswer(long chatId, long userId, int messageId, int joinMessageId) {
		sender.banUserInChat(chatId, userId, FilterHelper.getCurrentUnixTime() + config.getCaptchaBan());
		sender.deleteMessageInChat(chatId, joinMessageId);
		sender.deleteMessageInChat(chatId, messageId);
	}

	public void cleanCaptchaChecksMap(String key) {
		log.info(String.format("==> Stop CAPTCHA timer and remove key '%s' from HashMap.", key));
		captchaChecksMap.remove(key);
	}

	protected String generateKey(long chatId, long userId, String captchaMessageId) {
		return String.format("%d|%d|", chatId, userId) + captchaMessageId;
	}
}
