/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.CallbackQuery;

import static com.pengrad.telegrambot.model.Chat.Type.supergroup;
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

	private MessageEntity[] getTwoEntities(MessageEntity.Type type1, MessageEntity.Type type2,
	                                       int offset1, int length1, int offset2, int length2) {
		MessageEntity[] entities = new MessageEntity[2];

		MessageEntity entity1 = new MessageEntity(type1, offset1, length1);
//		setField(entity1, "type", type1);
//		setField(entity1, "offset", offset1);
//		setField(entity1, "length", length1);
		entities[0] = entity1;

		MessageEntity entity2 = new MessageEntity(type2, offset2, length2);
//		setField(entity2, "type", type2);
//		setField(entity2, "offset", offset2);
//		setField(entity2, "length", length2);
		entities[1] = entity2;

		return entities;
	}

	private MessageEntity[] getOneEntity(MessageEntity.Type type, int offset, int length) {
		MessageEntity[] entities = new MessageEntity[1];
		MessageEntity entity = new MessageEntity(type, offset, length);
//		setField(entity, "type", type);
//		setField(entity, "offset", offset);
//		setField(entity, "length", length);
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

	public Message getSimpleMessageAdmin(String text, String username, long chatId) {
		Message message = getSimpleMessage(text, username);

		setField(message, "from", getAdmin(username));
		setField(message, "chat", getChat(chatId));

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
		User user = new User(100L);
//		setField(user, "id", 100L);
		setField(user, "username", username);
		return user;
	}

	public User getAdmin(String username) {
		User user = new User(87336977L);
//		setField(user, "id", 87336977L);
		setField(user, "username", username);
		return user;
	}

	public Chat getChat() {
		return getChat(4242L);
	}

	public Chat getChat(long chatId) {
		Chat chat = new Chat();
		setField(chat, "id", chatId);
		setField(chat, "type", supergroup);
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
		return getCallbackQueryUsername(data, username, null, null);
	}

	public CallbackQuery getCallbackQueryUsername(String data, String username, Long userId, Long chatId) {
		CallbackQuery callbackQuery = new CallbackQuery();
		if (chatId != null) {
			setField(callbackQuery, "message", getSimpleMessageAdmin("test", "anyone", chatId));
		} else {
			setField(callbackQuery, "message", getSimpleMessage("test", "anyone"));
		}
		setField(callbackQuery, "data", data);

		User user = new User((userId != null) ? userId : 42L);
//		if (userId != null) {
//			setField(user, "id", userId);
//		} else {
//			setField(user, "id", 42L);
//		}
		setField(user, "username", username);
		setField(callbackQuery, "from", user);

		return callbackQuery;
	}
}
