package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.keyboard.impl.SubscribeKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class SubscribeCommand extends BotAbility {
	private final SubscribeKeyboard keyboard;

	public SubscribeCommand(SubscribeKeyboard keyboard) {
		this.keyboard = keyboard;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		Chat chat = message.chat();
		sender.replyKeyboard(chat.id(), message.messageId(),
			keyboard.getSubscribeStatusMessage(chat), keyboard.getMarkup());
	}
}
