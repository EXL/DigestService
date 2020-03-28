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
