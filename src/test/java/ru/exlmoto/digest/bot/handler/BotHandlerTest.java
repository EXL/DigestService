package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.MessageHelper;

import java.util.Map;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "bot.silent=true")
class BotHandlerTest {
	@Autowired
	private BotHandler handler;

	@Test
	public void testOnCommand() {
		handler.onCommand(new MessageHelper().getMessageWithEntities(
			"/start", bot_command, 0, "exlmoto"));
		handler.onCommand(new MessageHelper().getMessageWithEntities(
			"/hi", bot_command, 0, "exlmoto"));
		handler.onCommand(new MessageHelper().getMessageWithEntities(
			"/hi", bot_command, 0, "anyone"));
		handler.onCommand(new MessageHelper().getMessageWithEntities(
			"/unknown", bot_command, 0, "anyone"));
	}

	@Test
	public void testChopCallbackData() {
		assertEquals("chart_", handler.chopCallbackData("chart_rub"));
		assertEquals("chart_", handler.chopCallbackData("chart_rub_eur"));
		assertEquals("chart", handler.chopCallbackData("chart"));
		assertEquals("chart_", handler.chopCallbackData("chart___"));
		assertEquals("chart_", handler.chopCallbackData("chart__"));
		assertEquals("chart_", handler.chopCallbackData("chart_"));
		assertEquals("_", handler.chopCallbackData("_"));
		assertEquals("_", handler.chopCallbackData("_a"));
		assertEquals("_", handler.chopCallbackData("_ab"));
	}

	@Test
	public void testGetCallbackQueriesMap() {
		Map<Long, Long> map = handler.getCallbackQueriesMap();
		assertNotNull(map);
		assertEquals(0, map.size());
	}

	@Test
	public void testClearCallbackQueriesMap() {
		// TODO:
	}

	@Test
	public void testOnHashTag() {
		// TODO:
	}

	@Test
	public void testOnCallbackQuery() {
		// TODO:
	}

	@Test
	public void testOnKeyboard() {
		// TODO:
	}

	@Test
	public void testOnNewUsers() {
		// TODO:
	}

	@Test
	public void testOnLeftUser() {
		// TODO:
	}

	@Test
	public void testOnNewPhotos() {
		// TODO:
	}
}
