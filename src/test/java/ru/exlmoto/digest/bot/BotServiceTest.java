package ru.exlmoto.digest.bot;

import com.pengrad.telegrambot.model.Update;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(properties = "bot.log-updates=true" )
class BotServiceTest {
	@Autowired
	private BotService botService;

	@Autowired
	private BotConfiguration config;

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
