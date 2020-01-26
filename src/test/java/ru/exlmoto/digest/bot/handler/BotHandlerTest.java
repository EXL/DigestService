package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.MessageHelper;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;

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
			"/hi", bot_command, 0, "yakimka"));
		handler.onCommand(new MessageHelper().getMessageWithEntities(
			"/unknown", bot_command, 0, "yakimka"));
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
