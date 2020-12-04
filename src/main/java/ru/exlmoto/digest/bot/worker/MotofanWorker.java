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

package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.motofan.MotofanService;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.List;

@Component
public class MotofanWorker {
	private final Logger log = LoggerFactory.getLogger(MotofanWorker.class);

	private final MotofanService motofanService;
	private final DatabaseService databaseService;
	private final BotSender sender;
	private final BotConfiguration config;
	private final LocaleHelper locale;

	public MotofanWorker(MotofanService motofanService,
	                     DatabaseService databaseService,
	                     BotSender sender,
	                     BotConfiguration config,
	                     LocaleHelper locale) {
		this.motofanService = motofanService;
		this.databaseService = databaseService;
		this.sender = sender;
		this.config = config;
		this.locale = locale;
	}

	@Scheduled(cron = "${cron.bot.motofan.receiver}")
	public void workOnMotofanPosts() {
		try {
			List<BotSubMotofanEntity> subscribers = databaseService.getAllMotofanSubs();
			if (!subscribers.isEmpty()) {
				List<String> motofanPosts = motofanService.getLastMotofanPostsInHtml();
				if (!motofanPosts.isEmpty()) {
					sendNewMotofanPosts(motofanPosts, subscribers);
				}
			} else {
				log.info("=> Motofan subscriber list is empty, Motofan posts service disabled.");
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get Motofan subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Motofan Posts sender thread.", re);
		}
	}

	@Scheduled(cron = "${cron.bot.motofan.birthday}")
	public void sendGoodMorningWithBirthdays() {
		try {
			long motofanChatId = config.getMotofanChatId();
			if (databaseService.checkGreeting(motofanChatId)) {
				sender.sendHtml(motofanChatId, generateGoodMorningBirthdayReport());
			}
		} catch (DataAccessException dae) {
			log.error("Cannot check Greeting subscribe chat from database.", dae);
		}
	}

	protected String generateGoodMorningBirthdayReport() {
		String answer = locale.i18nR("bot.morning");
		String res = motofanService.getMotofanBirthdays();
		if (!StringUtils.isEmptyOrWhitespace(res)) {
			answer += "\n\n" + res;
		}
		return answer;
	}

	private void sendNewMotofanPosts(List<String> motofanPosts, List<BotSubMotofanEntity> subscribers) {
		motofanPosts.forEach(post -> subscribers.forEach(subscriber -> {
			long chatId = subscriber.getSubscription();
			log.info(String.format("=> Send Motofan Post to chat '%d', posts: '%d', subscribers: '%d'.",
				chatId, motofanPosts.size(), subscribers.size()));
			sender.sendHtml(chatId, post);
			try {
				Thread.sleep(config.getMessageDelay() * 1000L);
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		}));
	}
}
