package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.entity.SubMotofanEntity;
import ru.exlmoto.digest.motofan.MotofanService;
import ru.exlmoto.digest.repository.SubMotofanRepository;

import java.util.List;

@Component
public class MotofanWorker {
	private final Logger log = LoggerFactory.getLogger(MotofanWorker.class);

	private final MotofanService service;
	private final SubMotofanRepository repository;
	private final BotSender sender;
	private final BotConfiguration config;

	public MotofanWorker(MotofanService service,
	                     SubMotofanRepository repository,
	                     BotSender sender,
	                     BotConfiguration config) {
		this.service = service;
		this.repository = repository;
		this.sender = sender;
		this.config = config;
	}

	@Scheduled(cron = "${cron.bot.motofan.receiver}")
	public void workOnMotofanPosts() {
		try {
			List<String> motofanPosts = service.getLastMotofanPostsInHtml();
			if (!motofanPosts.isEmpty()) {
				sendNewMotofanPosts(motofanPosts, repository.findAll());
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get subscribe object from database.", dae);
		}
	}

	private void sendNewMotofanPosts(List<String> motofanPosts, List<SubMotofanEntity> subscribers) {
		new Thread(() -> motofanPosts.forEach(post -> subscribers.forEach(subscriber -> {
			long chatId = subscriber.getSubscription();
			log.info(String.format("=> Send Motofan Post to chat '%d', posts: '%d', subscribers: '%d'.",
				chatId, motofanPosts.size(), subscribers.size()));
			sender.sendHtmlMessage(chatId, post);
			try {
				Thread.sleep(config.getMessageDelay() * 1000);
			} catch (InterruptedException ie) {
				log.error("Cannot delay Motofan Posts sender thread.", ie);
			}
		}))).start();
	}
}
