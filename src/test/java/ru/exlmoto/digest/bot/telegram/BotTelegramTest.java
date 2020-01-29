package ru.exlmoto.digest.bot.telegram;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BotTelegramTest {
	@Autowired
	private BotTelegram telegram;

	@Autowired
	private BotConfiguration config;

	@Test
	public void testGetBot() {
		assertNotNull(telegram.getBot());
	}

	@Test
	public void testSaveTelegramBotSettings() {
		telegram.saveTelegramBotSettings();
	}

	@Test
	public void testBotParameters() {
		String name = telegram.getFirstName();
		String username = telegram.getUsername();
		int id = telegram.getId();

		String token = config.getToken();

		assertThat(name).isNotEmpty();
		assertThat(username).isNotEmpty();
		assertThat(id).isNotZero();
		assertThat(token).isNotEmpty();

		System.out.println(name);
		System.out.println(username);
		System.out.println(id);
		System.out.println(token);
	}
}
