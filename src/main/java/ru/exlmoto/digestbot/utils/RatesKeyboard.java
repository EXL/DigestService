package ru.exlmoto.digestbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class RatesKeyboard {
	private final YamlRatesIndexHelper mYamlRatesIndexHelper;

	@Autowired
	public RatesKeyboard(final YamlRatesIndexHelper aYamlRatesIndexHelper) {
		mYamlRatesIndexHelper = aYamlRatesIndexHelper;
	}

	public InlineKeyboardMarkup getRatesKeyboard() {
		final ArrayList<String> lButtonLabels = mYamlRatesIndexHelper.getButtonLabels();
		final ArrayList<String> lKeys = mYamlRatesIndexHelper.getKeys();

		final InlineKeyboardMarkup lInlineKeyboardMarkup = new InlineKeyboardMarkup();
		final List<List<InlineKeyboardButton>> lKeyboardMarkup = new ArrayList<>();
		List<InlineKeyboardButton> lKeyboardRow = new ArrayList<>();

		for (int i = 0; i < lButtonLabels.size(); i++) {
			lKeyboardRow.add(new InlineKeyboardButton(lButtonLabels.get(i)).setCallbackData(lKeys.get(i)));
		}
		lKeyboardMarkup.add(lKeyboardRow);
		lInlineKeyboardMarkup.setKeyboard(lKeyboardMarkup);
		return lInlineKeyboardMarkup;
	}

	public YamlRatesIndexHelper getYamlRatesIndexHelper() {
		return mYamlRatesIndexHelper;
	}
}
