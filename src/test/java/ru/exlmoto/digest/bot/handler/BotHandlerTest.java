package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.MessageHelper;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
