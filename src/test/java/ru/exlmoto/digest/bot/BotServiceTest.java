package ru.exlmoto.digest.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BotServiceTest {
	@Autowired
	private BotService botService;

	@Autowired
	private BotConfiguration config;

	@MockBean
	private BotSender botSender;

//	@BeforeEach
//	public void setUp() {
//		botService.setBotSender(botSender);
//	}

	@Test
	public void testTokenAndUsername() {
		String token = botService.getBotToken();
		String username = botService.getBotUsername();

		assertThat(token).isInstanceOf(String.class);
		assertThat(username).isInstanceOf(String.class);

		System.out.println(token);
		System.out.println(username);
	}

	@Test
	public void testManyUpdates() {
		manyUpdatesHelper(config.getMaxUpdates() * 2);
		manyUpdatesHelper(config.getMaxUpdates());
		manyUpdatesHelper(config.getMaxUpdates() - 1);
		manyUpdatesHelper(config.getMaxUpdates() + 1);
	}

	private void manyUpdatesHelper(int updatesCount) {
		List<Update> updates = new ArrayList<>();
		for (int i = 0; i < updatesCount; i++) {
			updates.add(new Update());
		}
		botService.onUpdatesReceived(updates);
		System.out.println("===");
	}
}
