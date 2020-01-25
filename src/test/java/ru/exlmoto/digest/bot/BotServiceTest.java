package ru.exlmoto.digest.bot;

import com.pengrad.telegrambot.model.Update;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BotServiceTest {
	@Autowired
	private BotService botService;

	@Autowired
	private BotConfiguration config;

	@SuppressWarnings("unused")
	@MockBean
	private BotSender botSender;

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
		botService.process(updates);
		System.out.println("===");
	}
}
