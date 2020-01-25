package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import javax.annotation.PostConstruct;

@Component
public class BotTelegram {
	private final Logger log = LoggerFactory.getLogger(BotTelegram.class);

	private final BotConfiguration config;

	private TelegramBot bot;
	private String username;

	public BotTelegram(BotConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	private void setUp() {
		bot = new TelegramBot(config.getToken());
		User botUser = bot.execute(new GetMe()).user();
		if (botUser != null) {
			username = botUser.username();
			log.info(String.format("Hello! My name is '%s'.", botUser.firstName()));
			log.info(String.format("My id is '%d'.", botUser.id()));
			log.info(String.format("And my username is '%s'.", username));
		}
	}

	public TelegramBot getBot() {
		return bot;
	}

	public String getUsername() {
		return username;
	}
}
