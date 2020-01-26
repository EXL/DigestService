package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class MessageHelper {
	public Message getMessageWithEntities(String entityText, MessageEntity.Type type, int offset, String username) {
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

	public Message getSimpleMessage(String text, String username) {
		Message message = new Message();
		setField(message, "text", text);
		setField(message, "message_id", 42);

		User user = new User();
		setField(user, "username", username);
		setField(message, "from", user);

		Chat chat = new Chat();
		setField(chat, "id", 4242L);
		setField(message, "chat", chat);

		return message;
	}
}
