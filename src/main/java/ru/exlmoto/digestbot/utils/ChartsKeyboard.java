package ru.exlmoto.digestbot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ChartsKeyboard {
	public static InlineKeyboardMarkup getChartsKeyboard(ArrayList<String> aButtonStrings) {
		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		// 4x5 from 20 elements.
		for (int i = 0; i < aButtonStrings.size(); ++i) {
			if (i % 4 == 0) {
				lKeyboardMarkup.add(lKeyboardRow);
				lKeyboardRow = new ArrayList<>();
			}
			lKeyboardRow.add(new InlineKeyboardButton(aButtonStrings.get(i)).setCallbackData(String.valueOf(i)));
		}
		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}
}
