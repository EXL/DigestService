package ru.exlmoto.digest.bot.worker;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.generator.DigestTgHtmlGenerator;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.repository.BotSubDigestRepository;

@Component
public class DigestWorker {
	private final Logger log = LoggerFactory.getLogger(DigestWorker.class);

	private final BotConfiguration config;
	private final BotSubDigestRepository repository;
	private final DigestTgHtmlGenerator htmlGenerator;

	public DigestWorker(BotConfiguration config,
	                    BotSubDigestRepository repository,
	                    DigestTgHtmlGenerator htmlGenerator) {
		this.config = config;
		this.repository = repository;
		this.htmlGenerator = htmlGenerator;
	}

	public void sendDigestToSubscribers(BotSender sender, Message message, String digest) {
		try {
			new Thread(() -> repository.findAll().forEach(subscriber -> {
				try {
					Thread.sleep(config.getMessageDelay() * 1000);
				} catch (InterruptedException ie) {
					throw new RuntimeException(ie);
				}
				sender.sendHtmlMessage(subscriber.getSubscription(),
					htmlGenerator.generateDigestMessageHtmlReport(message, digest));
			})).start();
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest subscribe object from database.", dae);
		} catch (RuntimeException re) {
			log.error("Cannot delay Digest message sender thread.", re);
		}
	}
}
