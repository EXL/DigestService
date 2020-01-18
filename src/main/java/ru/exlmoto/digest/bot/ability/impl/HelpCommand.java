package ru.exlmoto.digest.bot.ability.impl;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.util.BotHelper;

@Component
public class HelpCommand extends BotAbility {
	@Override
	protected void execute(BotHelper helper, Message message) {
		String botAnswer = helper.getLocale().i18n("bot.command.help");
		if (helper.isUserAdmin(helper.getValidUsername(message.getFrom()))) {
			botAnswer += "\n" + helper.getLocale().i18n("bot.command.help.admin");
		}
		helper.getSender().replyMessage(message.getChatId(), message.getMessageId(), botAnswer);
	}
}
