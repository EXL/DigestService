package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class HelpCommand extends BotAbility {
	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		String answer = locale.i18n("bot.command.help");
		if (helper.isUserAdmin(message.from().username())) {
			answer += "\n" + locale.i18n("bot.command.help.admin");
		}
		sender.replyMessage(message.chat().id(), message.messageId(), answer);
	}
}
