package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ru.exlmoto.digest.bot.ability.BotAbility;

public abstract class KeyboardAbility extends BotAbility<CallbackQuery> {
	public abstract InlineKeyboardMarkup getMarkup();
}
