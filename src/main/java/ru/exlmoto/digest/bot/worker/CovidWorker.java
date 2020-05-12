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

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.covid.CovidService;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.service.DatabaseService;

import java.util.Collections;
import java.util.List;

@Component
public class CovidWorker {
	private final Logger log = LoggerFactory.getLogger(CovidWorker.class);

	private final CovidService covidService;
	private final DatabaseService databaseService;
	private final BotSender sender;
	private final BotConfiguration config;

	public CovidWorker(CovidService covidService,
	                   DatabaseService databaseService,
	                   BotSender sender,
	                   BotConfiguration config) {
		this.covidService = covidService;
		this.databaseService = databaseService;
		this.sender = sender;
		this.config = config;
	}

	@Scheduled(cron = "${cron.bot.covid.report}")
	public void workOnCovidReport() {
		try {
			List<BotSubCovidEntity> subscribers = databaseService.getAllCovidSubs();
			if (!subscribers.isEmpty()) {
				String reportRu = covidService.tgHtmlRuReport();
				if (checkCovidReport(reportRu)) {
					sendCovidReport(reportRu, subscribers);
				}

				// Send Ukrainian COVID-2019 report only to MotoFan.Ru Telegram chat.
				long motofanChatId = config.getMotofanChatId();
				BotSubCovidEntity motofanChat = databaseService.getCovidSub(motofanChatId);
				if (motofanChat != null) {
					String reportUa = covidService.tgHtmlUaReport();
					if (checkCovidReport(reportUa)) {
						sendCovidReport(reportUa, motofanChatId);
					}
				}
			} else {
				log.info("Covid subscriber list is empty.");
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get Covid subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Covid report sender thread.", re);
		}
	}

	private boolean checkCovidReport(String report) {
		return report != null && !report.isEmpty();
	}

	private void sendCovidReport(String report, long chatId) {
		sendCovidReport(report, Collections.singletonList(new BotSubCovidEntity(chatId, "MotoFan.Ru")));
	}

	private void sendCovidReport(String report, List<BotSubCovidEntity> subscribers) {
		subscribers.forEach(subscriber -> {
			long chatId = subscriber.getSubscription();
			log.info(String.format("=> Send COVID-2019 report to chat '%d', subscribers: '%d'.",
				chatId, subscribers.size()));
			sender.sendHtml(chatId, report);
			try {
				Thread.sleep(config.getMessageDelay() * 1000);
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		});
	}
}
