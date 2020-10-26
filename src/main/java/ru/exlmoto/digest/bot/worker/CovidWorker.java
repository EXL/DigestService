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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.covid.CovidService;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.entity.BotSubCovidUaEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.Covid;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CovidWorker {
	private final Logger log = LoggerFactory.getLogger(CovidWorker.class);

	@Value("${covid.text.to.image}")
	private boolean covidTextToImage;

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
			List<Long> subscribersRu = convertCovidSubToListOfLongs(databaseService.getAllCovidSubs());
			List<Long> subscribersUa = convertCovidUaSubToListOfLongs(databaseService.getAllCovidUaSubs());
			boolean subscribersRuNotEmpty = !subscribersRu.isEmpty();
			boolean subscribersUaNotEmpty = !subscribersUa.isEmpty();
			if (subscribersRuNotEmpty || subscribersUaNotEmpty) {
				if (subscribersRuNotEmpty) {
					processCovidReport(subscribersRu, covidService.tgHtmlRuReport(), Covid.ru);
				}
				if (subscribersUaNotEmpty) {
					processCovidReport(subscribersUa, covidService.tgHtmlUaReport(), Covid.ua);
				}
			} else {
				log.info("=> Covid subscribers lists are empty, Covid reports service disabled.");
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get Covid subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Covid report sender thread.", re);
		}
	}

	private void processCovidReport(List<Long> subscribers, String report, Covid stat) {
		if (checkCovidReport(report)) {
			if (covidTextToImage) {
				File image = covidService.imageRenderedReport(report, stat);
				if (image != null) {
					sendCovidReport(image, subscribers, stat);
					if (!image.delete()) {
						log.warn(String.format("Cannot delete image file report (%s): '%s'.",
							stat.name(), image.getAbsolutePath()));
					}
				}
			} else {
				sendCovidReport(report, subscribers, stat);
			}
		}
	}

	private boolean checkCovidReport(String report) {
		return report != null && !report.isEmpty();
	}

	private void sendCovidReport(String report, long chatId, Covid stat) {
		sendCovidReport(report, Collections.singletonList(chatId), stat);
	}

	private void sendCovidReport(Object report, List<Long> subscribers, Covid stat) {
		subscribers.forEach(chatId -> {
			log.info(String.format("=> Send COVID-2019 (%s) report to chat '%d', subscribers: '%d'.",
				stat.name(), chatId, subscribers.size()));
			if (covidTextToImage) {
				sender.sendLocalPhotoToChat(chatId, (File)report, covidService.imageRenderedReportTitle(stat));
			} else {
				sender.sendHtml(chatId, (String)report);
			}
			try {
				Thread.sleep(config.getMessageDelay() * 1000);
			} catch (InterruptedException ie) {
				throw new RuntimeException(ie);
			}
		});
	}

	private List<Long> convertCovidSubToListOfLongs(List<BotSubCovidEntity> subCovidEntities) {
		List<Long> listOfLongs = new ArrayList<>();
		subCovidEntities.forEach(sub -> listOfLongs.add(sub.getSubscription()));
		return listOfLongs;
	}

	private List<Long> convertCovidUaSubToListOfLongs(List<BotSubCovidUaEntity> subCovidUaEntities) {
		List<Long> listOfLongs = new ArrayList<>();
		subCovidUaEntities.forEach(sub -> listOfLongs.add(sub.getSubscription()));
		return listOfLongs;
	}
}
