package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.CallbackQuery;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

public abstract class KeyboardAbility extends BotAbility<CallbackQuery> {
	@Override
	protected abstract void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback);
}
