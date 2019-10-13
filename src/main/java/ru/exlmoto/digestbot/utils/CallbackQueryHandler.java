package ru.exlmoto.digestbot.utils;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import ru.exlmoto.digestbot.DigestBot;

public class CallbackQueryHandler {
	public void handle(final DigestBot aDigestBot, final CallbackQuery aCallbackQuery) {
		new Thread(() -> {
			if (aCallbackQuery.getData().startsWith("chart")) {
				aDigestBot.getChartsKeyboard().handleRatesKeyboard(aDigestBot, aCallbackQuery);
			}
		}).start();
	}
}
