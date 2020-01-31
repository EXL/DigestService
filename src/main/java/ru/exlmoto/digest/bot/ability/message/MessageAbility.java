package ru.exlmoto.digest.bot.ability.message;

import com.pengrad.telegrambot.model.Message;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

public abstract class MessageAbility extends BotAbility<Message> {
	@Override
	protected abstract void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message);
}
