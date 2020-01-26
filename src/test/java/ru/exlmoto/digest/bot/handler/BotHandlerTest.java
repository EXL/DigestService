package ru.exlmoto.digest.bot.handler;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.MessageEntity.Type;
import com.pengrad.telegrambot.model.User;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;

import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(properties = "bot.silent=true")
class BotHandlerTest {
	@Autowired
	private BotHandler handler;

	@Test
	public void testOnCommand() {
		handler.onCommand(getMessageHelper("/start", bot_command, 0, "exlmoto"));
		handler.onCommand(getMessageHelper("/hi", bot_command, 0, "exlmoto"));
		handler.onCommand(getMessageHelper("/hi", bot_command, 0, "yakimka"));
		handler.onCommand(getMessageHelper("/unknown", bot_command, 0, "yakimka"));
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

	private Message getMessageHelper(String entityText, Type type, int offset, String username) {
		Message message = new Message();
		setField(message, "text", entityText);
		setField(message, "message_id", 42);

		MessageEntity[] entities = new MessageEntity[1];
		MessageEntity entity = new MessageEntity();
		setField(entity, "type", type);
		setField(entity, "offset", offset);
		setField(entity, "length", entityText.length());
		entities[0] = entity;
		setField(message, "entities", entities);

		User user = new User();
		setField(user, "username", username);
		setField(message, "from", user);

		Chat chat = new Chat();
		setField(chat, "id", 4242L);
		setField(message, "chat", chat);

		return message;
	}
}
