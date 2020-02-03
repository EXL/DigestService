package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

public abstract class KeyboardAbility extends BotAbility<CallbackQuery> {
	public abstract InlineKeyboardMarkup getMarkup(int page, int totalPages);

	@Override
	protected abstract void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback);
}
