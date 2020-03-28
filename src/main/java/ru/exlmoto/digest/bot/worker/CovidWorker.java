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
			String report = covidService.tgHtmlReport();
			if (report != null && !report.isEmpty()) {
				sendCovidReport(report, databaseService.getAllCovidSubs());
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get Covid subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Covid report sender thread.", re);
		}
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
