package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.User;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CallbackQueryHelper {
	public CallbackQuery getCallbackQuery(String data) {
		CallbackQuery callbackQuery = new CallbackQuery();
		setField(callbackQuery, "message", new MessageHelper().getSimpleMessage("test", "anyone"));
		setField(callbackQuery, "data", data);
		return callbackQuery;
	}

	public CallbackQuery getCallbackQueryUsername(String data, String username) {
		CallbackQuery callbackQuery = new CallbackQuery();
		setField(callbackQuery, "message", new MessageHelper().getSimpleMessage("test", "anyone"));
		setField(callbackQuery, "data", data);

		User user = new User();
		setField(user, "username", username);
		setField(callbackQuery, "from", user);

		return callbackQuery;
	}
}
