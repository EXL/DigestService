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

import javax.annotation.PostConstruct;

import java.util.List;

@Component
public class ChartsKeyboard extends BotKeyboard {
	private final ChartService chart;

	private InlineKeyboardMarkup markup = null;

	public ChartsKeyboard(ChartService chart) {
		this.chart = chart;
	}

	@PostConstruct
	private void generateKeyboard() {
		List<String> keys = chart.getChartKeys();

		final int columns = 4;
		final int size = keys.size();
		final int rows = ((size - 1) / columns) + 1;

		InlineKeyboardButton[][] keyboard = new InlineKeyboardButton[rows][];
		for (int i = 0; i < rows; i++) {
			int columnLength = columns - ((columns * rows) - size) * (rows / (rows * (rows - i)));
			// int columnLength = (i == rows - 1) ? columns - ((columns * rows) - size) : columns;
			InlineKeyboardButton[] keyboardRow = new InlineKeyboardButton[columnLength];
			for (int j = 0; j < columnLength; j++) {
				String key = keys.get(columns * i + j);
				keyboardRow[j] = new InlineKeyboardButton(key).callbackData(CHART + key);
			}
			keyboard[i] = keyboardRow;
		}
		markup = new InlineKeyboardMarkup(keyboard);
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return markup;
	}

	@Override
	protected void handle(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery callback) {
		sender.sendCallbackQueryAnswer(callback.id(), "Here!");
	}
}
