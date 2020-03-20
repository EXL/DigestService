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
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.service.DatabaseService;

import java.util.List;

@Component
public class DigestWorker {
	private final Logger log = LoggerFactory.getLogger(DigestWorker.class);

	private final BotConfiguration config;
	private final BotHelper helper;
	private final DatabaseService service;
	private final DigestTgHtmlGenerator htmlGenerator;

	public DigestWorker(BotConfiguration config,
	                    BotHelper helper,
	                    DatabaseService service,
	                    DigestTgHtmlGenerator htmlGenerator) {
		this.config = config;
		this.helper = helper;
		this.service = service;
		this.htmlGenerator = htmlGenerator;
	}

	@Scheduled(cron = "${cron.bot.digest.shredder}")
	public void obsoleteDataShredder() {
		log.info("=> Start drop obsolete data from database.");

		try {
			service.dropObsoleteDigests(helper.getCurrentUnixTime() - config.getObsoleteDataDelay(),
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
	}

	public void sendDigestToSubscribers(BotSender sender, Message message, String digest) {
		try {
			List<BotSubDigestEntity> subscribers = service.getAllDigestSubs();
			new Thread(() -> subscribers.forEach(subscriber -> {
				try {
					Thread.sleep(config.getMessageDelay() * 1000);
				} catch (InterruptedException ie) {
					throw new RuntimeException(ie);
				}
				long subscription = subscriber.getSubscription();
				log.info(String.format("=> Send Digest Message to chat '%s', id: '%d', subscribers: '%d'.",
					subscriber.getName(), subscription, subscribers.size()));
				sender.sendHtml(subscription, htmlGenerator.generateDigestMessageHtmlReport(message, digest));
			})).start();
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Runtime exception on Digest message sender thread.", re);
		}
	}
}
