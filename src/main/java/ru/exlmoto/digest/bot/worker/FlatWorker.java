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
import ru.exlmoto.digest.entity.FlatSetupEntity;
import ru.exlmoto.digest.flat.FlatService;
import ru.exlmoto.digest.service.DatabaseService;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlatWorker {
	private final Logger log = LoggerFactory.getLogger(FlatWorker.class);

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
			List<String> reports = new ArrayList<>();
			databaseService.getFlatSettings().ifPresent(settings -> {
				if (databaseService.checkFlatSettings(settings)) {
					addCianReportToList(settings, reports);
					addN1ReportToList(settings, reports);

					sendFlatReports(getSubscriberIds(settings.getSubscribeIds()), reports);
				} else {
					log.error("Flat settings checks failed, sending reports disabled.");
				}
			});
		} catch (DataAccessException dae) {
			log.error("Cannot get flat links settings from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Flat report sender thread.", re);
		}
	}

	private void sendFlatReports(List<Long> chatIds, List<String> reports) {
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

	protected List<Long> getSubscriberIds(String subscriberIds) {
		List<Long> subscribers = new ArrayList<>();
		if (StringUtils.hasText(subscriberIds)) {
			String parsed = subscriberIds.replaceAll("\\s+", ",");
			if (parsed.contains(",")) {
				String[] ids = parsed.split(",");
				for (String id : ids) {
					addIdToList(id, subscribers);
				}
			} else {
				addIdToList(parsed, subscribers);
			}
		}
		return subscribers;
	}

	private void addIdToList(String subscriberId, List<Long> subscribers) {
		if (StringUtils.hasText(subscriberId)) {
			try {
				subscribers.add(Long.parseLong(subscriberId));
			} catch (NumberFormatException nfe) {
				log.error(String.format("Cannot parse '%s' value as Long.", subscriberId), nfe);
			}
		}
	}

	private void addCianReportToList(FlatSetupEntity settings, List<String> reports) {
		String apiCianUrl = settings.getApiCianUrl();
		if (StringUtils.hasText(apiCianUrl)) {
			addReportToList(flatService.tgHtmlReportCian(apiCianUrl,
				settings.getViewCianUrl(), settings.getMaxVariants()), reports);
		}
	}

	private void addN1ReportToList(FlatSetupEntity settings, List<String> reports) {
		String apiN1Url = settings.getApiN1Url();
		if (StringUtils.hasText(apiN1Url)) {
			addReportToList(flatService.tgHtmlReportN1(settings.getApiN1Url(),
				settings.getViewN1Url(), settings.getMaxVariants()), reports);
		}
	}

	private void addReportToList(String report, List<String> reports) {
		if (StringUtils.hasText(report)) {
			reports.add(report);
		}
	}
}
