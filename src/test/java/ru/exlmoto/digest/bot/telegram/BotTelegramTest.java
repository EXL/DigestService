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
	public void testUsernameAndToken() {
		String username = telegram.getUsername();
		String token = config.getToken();

		assertThat(username).isNotEmpty();
		assertThat(token).isNotEmpty();

		System.out.println(username);
		System.out.println(token);
	}
}
