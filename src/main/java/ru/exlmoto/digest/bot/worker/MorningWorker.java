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

package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class MorningWorker {
	private final Logger log = LoggerFactory.getLogger(MorningWorker.class);

	private final DatabaseService service;
	private final BotSender sender;
	private final LocaleHelper locale;
	private final BotConfiguration config;

	public MorningWorker(DatabaseService service, BotSender sender, LocaleHelper locale, BotConfiguration config) {
		this.service = service;
		this.sender = sender;
		this.locale = locale;
		this.config = config;
	}

	@Scheduled(cron = "${cron.bot.morning.send}")
	public void sendGoodMorning() {
		try {
			long motofanChatId = config.getMotofanChatId();
			if (service.checkGreeting(motofanChatId)) {
				sender.sendHtml(motofanChatId, locale.i18nR("bot.morning"));
			}
		} catch (DataAccessException dae) {
			log.error("Cannot check Greeting subscribe chat from database.", dae);
		}
	}
}
