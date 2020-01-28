package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.GetMeResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import javax.annotation.PostConstruct;

@Component
public class BotTelegram {
	private final Logger log = LoggerFactory.getLogger(BotTelegram.class);

	private final BotConfiguration config;

	private TelegramBot bot;
	private String username;
	private String firstName;
	private int id;

	public BotTelegram(BotConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	private void setUp() {
		log.info("=> Start initialize Telegram Bot...");

		bot = new TelegramBot(config.getToken());
		GetMeResponse response = bot.execute(new GetMe());
		if (response.isOk()) {
			User botUser = response.user();
			Assert.notNull(botUser, "Cannot initialize Telegram Bot, bot user is null.");

			username = botUser.username();
			firstName = botUser.firstName();
			id = botUser.id();

			log.info(String.format("==> Hello! My name is '%s'.", firstName));
			log.info(String.format("==> My id is '%d'.", id));
			log.info(String.format("==> And my username is '%s'.", username));

			log.info("=> End initialize Telegram Bot.");
		} else {
			throw new IllegalStateException(String.format("Cannot initialize Telegram Bot, error: '%d, %s'.",
				response.errorCode(), response.description()));
		}
	}

	public TelegramBot getBot() {
		return bot;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public int getId() {
		return id;
	}
}
