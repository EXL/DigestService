package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public abstract class KeyboardSimpleAbility extends KeyboardAbility {
	public abstract InlineKeyboardMarkup getMarkup();
}
