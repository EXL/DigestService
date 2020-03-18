package ru.exlmoto.digest.bot.ability.message;

import com.pengrad.telegrambot.model.Message;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

public abstract class MessageModerAbility extends MessageAdminAbility {
	@Override
	protected boolean checkRights(BotHelper helper, BotSender sender, Message message) {
		return sender.isUserChatAdministrator(message.chat().id(), message.from().id());
	}

	@Override
	protected abstract void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message);
}
