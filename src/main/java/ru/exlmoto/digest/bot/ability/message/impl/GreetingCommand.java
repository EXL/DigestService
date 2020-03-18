package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.impl.GreetingKeyboard;
import ru.exlmoto.digest.bot.ability.message.MessageModerAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class GreetingCommand extends MessageModerAbility {
	private final GreetingKeyboard keyboard;

	public GreetingCommand(GreetingKeyboard keyboard) {
		this.keyboard = keyboard;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		keyboard.processGreetingStatusMessage(message.chat().id(), message.messageId(), false, sender);
	}
}
