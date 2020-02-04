package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

import java.util.List;

@Component
public class ChartKeyboard extends KeyboardSimpleAbility {
	private final ChartService chartService;

	private InlineKeyboardMarkup markup = null;

	public ChartKeyboard(ChartService chartService) {
		this.chartService = chartService;
	}

	@PostConstruct
	private void generateKeyboard() {
		List<String> keys = chartService.getChartKeys();

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
				keyboardRow[j] = new InlineKeyboardButton(chartService.getButtonLabel(key))
					.callbackData(Keyboard.chart.withName() + key);
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
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();
		int messageId = message.messageId();

		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		sender.sendCallbackQueryAnswer(callback.id(),
			locale.i18n("bot.inline.selected") + " " + chartService.getTitle(key));

		Answer<Chart> res = chartService.getChart(key);
		if (res.ok()) {
			Chart chart = res.answer();
			sender.replyPhoto(chatId, messageId, chart.getPath(), chart.getTitle());
		} else {
			sender.replyMarkdown(chatId, messageId, String.format(locale.i18n("bot.error.chart"), res.error()));
		}
	}
}
