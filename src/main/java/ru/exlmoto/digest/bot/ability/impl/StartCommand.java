package ru.exlmoto.digest.bot.ability.impl;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.util.BotHelper;

@Component
public class StartCommand extends BotAbility {
	@Override
	protected void execute(BotHelper helper, Message message) {
		helper.getSender().replyMessage(message.getChatId(), message.getMessageId(),
			helper.getLocale().i18n("bot.command.start"));
	}
}
