package ru.exlmoto.digest.bot.ability.message;

import com.pengrad.telegrambot.model.Message;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

public abstract class MessageAdminAbility extends MessageAbility {
	@Override
	public void process(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		if (helper.isUserAdmin(message.from().username())) {
			execute(helper, sender, locale, message);
		} else {
			sender.replyMessage(message.chat().id(), message.messageId(),
				locale.i18nU("bot.error.access", helper.getValidUsername(message.from())));
		}
	}

	@Override
	protected abstract void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message);
}
