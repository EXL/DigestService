/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
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

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.generator.DigestTgHtmlGenerator;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.List;

@Component
public class DigestWorker {
	private final Logger log = LoggerFactory.getLogger(DigestWorker.class);

	private final BotConfiguration config;
	private final FilterHelper filter;
	private final DatabaseService service;
	private final DigestTgHtmlGenerator htmlGenerator;

	public DigestWorker(BotConfiguration config,
	                    FilterHelper filter,
	                    DatabaseService service,
	                    DigestTgHtmlGenerator htmlGenerator) {
		this.config = config;
		this.filter = filter;
		this.service = service;
		this.htmlGenerator = htmlGenerator;
	}

	@Scheduled(cron = "${cron.bot.digest.shredder}")
	public void obsoleteDataShredder() {
		if (config.isDigestShredder()) {
			log.info("=> Start drop obsolete data from database.");

			try {
				service.dropObsoleteDigests(filter.getCurrentUnixTime() - config.getObsoleteDataDelay(),
					config.getMotofanChatId());

				List<Long> usersId = service.getAllUserIds();
				service.getAllDigestUsers().forEach(digestUserEntity -> {
					long userId = digestUserEntity.getId();
					if (!usersId.contains(userId)) {
						log.info(String.format("==> Delete obsolete user '%s' with id '%d'.",
							digestUserEntity.getUsername(), userId));
						service.deleteDigestUser(userId);
					}
				});
			} catch (DataAccessException dae) {
				log.error("Database error while dropping obsolete data.", dae);
			}

			log.info("=> End drop obsolete data from database.");
		} else {
			log.info("=> Digests shredder disabled in properties.");
		}
	}

	public void sendDigestToSubscribers(BotSender sender, Message message, String digest) {
		long userId = message.from().id();
		try {
			List<BotSubDigestEntity> subscribers = service.getAllDigestSubs();
			if (!subscribers.isEmpty()) {
				new Thread(() -> subscribers.forEach(subscriber -> {
					try {
						Thread.sleep(config.getMessageDelay() * 1000L);
					} catch (InterruptedException ie) {
						throw new RuntimeException(ie);
					}
					long subscription = subscriber.getSubscription();
					if (subscription != userId) {
						log.info(String.format("=> Send Digest Message to chat '%s', id: '%d', subscribers: '%d'.",
								subscriber.getName(), subscription, subscribers.size()));
						sender.sendHtml(subscription, htmlGenerator.generateDigestMessageHtmlReport(message, digest));
					} else {
						log.info(String.format("=> Ignoring Send Digest Message to chat '%s', " +
							"id==userId: '%d'=='%d', subscribers: '%d'.",
								subscriber.getName(), subscription, userId, subscribers.size()));
					}
				})).start();
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Digest message sender thread.", re);
		}
	}
}
