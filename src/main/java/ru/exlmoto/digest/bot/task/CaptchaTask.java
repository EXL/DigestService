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

package ru.exlmoto.digest.bot.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.util.Pair;

import ru.exlmoto.digest.bot.ability.keyboard.impl.CaptchaKeyboard;

public class CaptchaTask implements Runnable {
	private final Logger log = LoggerFactory.getLogger(CaptchaTask.class);

	private final CaptchaKeyboard keyboard;
	private final String key;
	private final Pair<Long, Long> messageIds;

	public static final int CAPTCHA_CHAT_ID    = 0;
	public static final int CAPTCHA_USER_ID    = 1;
	public static final int CAPTCHA_MESSAGE_ID = 2;
	public static final int JOINED_MESSAGE_ID  = 3;

	public CaptchaTask(CaptchaKeyboard keyboard, String key, Pair<Long, Long> messageIds) {
		this.keyboard = keyboard;
		this.key = key;
		this.messageIds = messageIds;
	}

	@Override
	public void run() {
		long chatId = getIdFromKey(key, CAPTCHA_CHAT_ID);
		long userId = getIdFromKey(key, CAPTCHA_USER_ID);
		int captchaMessageId = Math.toIntExact(getIdFromKey(key, CAPTCHA_MESSAGE_ID));
		int joinedMessageId = Math.toIntExact(messageIds.getFirst()); // JOINED_MESSAGE_ID
		log.info(String.format("===> Run CAPTCHA Task: { chatId=%d, userId=%d, " +
			"captchaMessageId=%d, joinedMessageId=%d }.", chatId, userId, captchaMessageId, joinedMessageId));
		keyboard.processWrongAnswer(chatId, userId, captchaMessageId, joinedMessageId);
		keyboard.cleanCaptchaChecksMap(key);
	}

	protected long getIdFromKey(String key, int id) {
		try {
			String[] tokens = key.split("\\|");
			if (id < tokens.length)
				return Long.parseLong(tokens[id]);
		} catch (Exception e) {
			log.error(String.format("Cannot get id from key '%s'!", key), e);
		}
		return 0L;
	}
}
