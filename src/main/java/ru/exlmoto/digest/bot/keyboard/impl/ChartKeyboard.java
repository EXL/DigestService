package ru.exlmoto.digest.bot.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.util.List;

@Component
public class ChartKeyboard extends BotKeyboard {
	private final ChartService chart;

	public ChartKeyboard(ChartService chart) {
		this.chart = chart;
	}

	@Override
	protected InlineKeyboardMarkup getMarkup() {
		List<String> keys = chart.getChartKeys();

		final int columns = 4;
		final int rows = keys.size() / columns;

		InlineKeyboardButton[][] keyboard = new InlineKeyboardButton[rows][columns];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				String key = keys.get(columns * i);
				keyboard[i][j] = new InlineKeyboardButton(key).callbackData(CHART + key);
				System.out.println(keyboard[i][j].toString());
			}
		}

		return new InlineKeyboardMarkup(keyboard);
	}

	@Override
	protected void handle(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		System.out.println("Here");
	}
}
