package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.repository.BotSubDigestRepository;

@Component
public class DigestWorker {
	private final Logger log = LoggerFactory.getLogger(DigestWorker.class);

	private final BotConfiguration config;
	private final BotSubDigestRepository repository;

	public DigestWorker(BotConfiguration config, BotSubDigestRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	public void sendDigestToSubscribers(BotSender sender) {
		try {
			new Thread(() -> repository.findAll().forEach(subscriber -> {
				sender.sendHtmlMessage(subscriber.getSubscription(), "");
				try {
					Thread.sleep(config.getMessageDelay() * 1000);
				} catch (InterruptedException ie) {
					throw new RuntimeException(ie);
				}
			})).start();
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Cannot delay Digest message sender thread.", re);
		}
	}
}
