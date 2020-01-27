package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.CallbackQuery;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CallbackQueryHelper {
	public CallbackQuery getCallbackQuery(String data) {
		CallbackQuery callbackQuery = new CallbackQuery();
		setField(callbackQuery, "message", new MessageHelper().getSimpleMessage("test", "anyone"));
		setField(callbackQuery, "data", data);
		return callbackQuery;
	}
}
