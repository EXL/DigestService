package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "bot.silent=true")
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
	public void testProcessTelegramBotSettings() {
		telegram.createTelegramBotSettings();
		telegram.updateTelegramBotSettings();
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

	@Test
	public void testChatAdministrators() {
		// assertFalse(adminListHelper(telegram.chatAdministrators(-1001148683293L)).isEmpty());
		// assertTrue(adminListHelper(telegram.chatAdministrators(-1001045117849L)).isEmpty());
		assertTrue(adminListHelper(telegram.chatAdministrators(-100104511784134569L)).isEmpty());
		assertTrue(adminListHelper(telegram.chatAdministrators(87336977L)).isEmpty());
	}

	private List<ChatMember> adminListHelper(GetChatAdministratorsResponse response) {
		if (response != null && response.isOk()) {
			return response.administrators();
		}
		return new ArrayList<>();
	}
}
