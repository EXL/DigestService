package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class HelpCommand extends MessageAbility {
	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		String answer = locale.i18n("bot.command.help");
		if (helper.isUserAdmin(message.from().username())) {
			answer += "\n" + locale.i18n("bot.command.help.admin");
		}
		sender.replyMarkdown(message.chat().id(), message.messageId(), answer);
	}
}
