package ru.exlmoto.digest.bot.ability;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digest.bot.util.BotHelper;

public abstract class BotAdminAbility extends BotAbility {
	@Override
	protected void processAux(BotHelper helper, Message message) {
		if (helper.isUserAdmin(message.getFrom().getUserName())) {
			execute(helper, message);
		} else {
			helper.getSender().replyMessage(message.getChatId(), message.getMessageId(),
				helper.getLocale().i18nU("bot.error.access", helper.getValidUsername(message.getFrom())));
		}
	}
}
