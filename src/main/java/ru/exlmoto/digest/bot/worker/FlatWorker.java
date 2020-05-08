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
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.flat.FlatService;
import ru.exlmoto.digest.service.DatabaseService;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlatWorker {
	private final Logger log = LoggerFactory.getLogger(FlatWorker.class);

	private final String usernameWithAt = "@exlmoto";

	private final FlatService flatService;
	private final DatabaseService databaseService;
	private final BotSender sender;
	private final BotConfiguration config;

	public FlatWorker(FlatService flatService,
	                  DatabaseService databaseService,
	                  BotSender sender,
	                  BotConfiguration config) {
		this.flatService = flatService;
		this.databaseService = databaseService;
		this.sender = sender;
		this.config = config;
	}

	@Scheduled(cron = "${cron.flat.report.send}")
	public void workOnFlatReport() {
		try {
			List<Long> chatIds = new ArrayList<>();
			BotDigestUserEntity user = databaseService.getDigestUserNullable(usernameWithAt);
			if (user != null) {
				chatIds.add(user.getId());
			}

			List<String> reports = new ArrayList<>();
			databaseService.getFlatSettings().ifPresent(settings -> {
				if (databaseService.checkFlatSettings(settings)) {
					String apiCianUrl = settings.getApiCianUrl();
					if (StringUtils.hasText(apiCianUrl)) {
						String cianReport = flatService.tgHtmlReportCian(apiCianUrl,
							settings.getViewCianUrl(), settings.getMaxVariants());
						if (StringUtils.hasText(cianReport)) {
							reports.add(cianReport);
						}
					}

					String apiN1Url = settings.getApiN1Url();
					if (StringUtils.hasText(apiN1Url)) {
						String n1Report = flatService.tgHtmlReportN1(settings.getApiN1Url(),
							settings.getViewN1Url(), settings.getMaxVariants());
						if (StringUtils.hasText(n1Report)) {
							reports.add(n1Report);
						}
					}
				} else {
					log.error("Flat settings checks failed.");
				}
			});

			if (!reports.isEmpty() && !chatIds.isEmpty()) {
				sendFlatReports(chatIds, reports);
			} else {
				log.error(String.format("Cannot send flat reports. Report List '%d' or Chat List '%d' are empty!",
					reports.size(), chatIds.size()));
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get flat links settings from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Flat report sender thread.", re);
		}
	}

	public void sendFlatReports(List<Long> chatIds, List<String> reports) {
		chatIds.forEach(chatId -> reports.forEach(report -> {
			log.info(String.format("=> Send Flat report to chat '%d'.", chatId));
			sender.sendHtml(chatId, report);
			try {
				Thread.sleep(config.getMessageDelay() * 1000);
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		}));
	}
}
