package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.MessageEntity.Type;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.MessageEntity.Type.hashtag;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class UpdateHelper {
	public Message getCommand(String text, String username) {
		Message message = getSimpleMessage(text, username);
		setField(message, "entities", getOneEntity(bot_command, 0, text.length()));
		return message;
	}

	public Message getHashTag(String text, int offset, int length, String username) {
		Message message = getSimpleMessage(text, username);
		setField(message, "entities", getOneEntity(hashtag, offset, length));
		return message;
	}

	public Message getTwoHashTags(String text, int offset1, int length1, int offset2, int length2, String username) {
		Message message = getSimpleMessage(text, username);
		setField(message, "entities", getTwoEntities(hashtag, hashtag, offset1, length1, offset2, length2));
		return message;
	}

	private MessageEntity[] getTwoEntities(Type type1, Type type2, int offset1, int length1, int offset2, int length2) {
		MessageEntity[] entities = new MessageEntity[2];

		MessageEntity entity1 = new MessageEntity();
		setField(entity1, "type", type1);
		setField(entity1, "offset", offset1);
		setField(entity1, "length", length1);
		entities[0] = entity1;

		MessageEntity entity2 = new MessageEntity();
		setField(entity2, "type", type2);
		setField(entity2, "offset", offset2);
		setField(entity2, "length", length2);
		entities[1] = entity2;

		return entities;
	}

	private MessageEntity[] getOneEntity(Type type, int offset, int length) {
		MessageEntity[] entities = new MessageEntity[1];
		MessageEntity entity = new MessageEntity();
		setField(entity, "type", type);
		setField(entity, "offset", offset);
		setField(entity, "length", length);
		entities[0] = entity;
		return entities;
	}

	public Message getSimpleMessage(String text, String username) {
		Message message = new Message();
		setField(message, "text", text);
		setField(message, "message_id", 42);
		setField(message, "date", 42);

		setField(message, "from", getUser(username));
		setField(message, "chat", getChat());

		return message;
	}

	public Message getSimpleMessageAdmin(String text, String username) {
		Message message = getSimpleMessage(text, username);
		setField(message, "from", getAdmin(username));
		return message;
	}

	public Message getNewUsersWithUsername(String username) {
		Message message = getSimpleMessage("Text", "Username");
		User[] users = new User[3];
		users[0] = getUser("NewUser1");
		users[1] = getUser(username);
		users[2] = getUser("NewUser3");
		setField(message, "new_chat_members", users);
		return message;
	}

	public Message getNewUsers(int count) {
		Message message = getSimpleMessage("Text", "Username");
		User[] users = new User[count];
		for (int i = 0; i < count; i++) {
			users[i] = getUser("NewUser" + (i + 1));
		}
		setField(message, "new_chat_members", users);
		return message;
	}

	public Message getLeftUser(String username) {
		Message message = getSimpleMessage("Text", "Username");
		setField(message, "left_chat_member", getUser(username));
		return message;
	}

	public Message getNewPhotos(int count) {
		Message message = getSimpleMessage("Text", "Username");
		PhotoSize[] photos = new PhotoSize[count];
		for (int i = 0; i < count; i++) {
			photos[i] = new PhotoSize();
		}
		setField(message, "new_chat_photo", photos);
		return message;
	}

	public User getUser(String username) {
		User user = new User();
		setField(user, "id", 100);
		setField(user, "username", username);
		return user;
	}

	public User getAdmin(String username) {
		User user = new User();
		setField(user, "id", 87336977);
		setField(user, "username", username);
		return user;
	}

	public Chat getChat() {
		Chat chat = new Chat();
		setField(chat, "id", 4242L);
		setField(chat, "type", Chat.Type.supergroup);
		setField(chat, "title", "Chat Title");
		return chat;
	}

	public CallbackQuery getCallbackQuery(String data) {
		CallbackQuery callbackQuery = new CallbackQuery();
		setField(callbackQuery, "message", new UpdateHelper().getSimpleMessage("test", "anyone"));
		setField(callbackQuery, "data", data);
		return callbackQuery;
	}

	public CallbackQuery getCallbackQueryUsername(String data, String username) {
		CallbackQuery callbackQuery = new CallbackQuery();
		setField(callbackQuery, "message", new UpdateHelper().getSimpleMessage("test", "anyone"));
		setField(callbackQuery, "data", data);

		User user = new User();
		setField(user, "username", username);
		setField(callbackQuery, "from", user);

		return callbackQuery;
	}
}
