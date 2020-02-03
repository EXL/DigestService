package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class StartCommand extends MessageAbility {
	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		sender.replyMessage(message.chat().id(), message.messageId(), locale.i18n("bot.command.start"));
	}
}
