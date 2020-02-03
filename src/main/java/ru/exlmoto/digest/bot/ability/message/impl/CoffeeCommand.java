package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class CoffeeCommand extends MessageAbility {
	private final BotConfiguration config;

	public CoffeeCommand(BotConfiguration config) {
		this.config = config;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		sender.replySticker(message.chat().id(), message.messageId(), config.getStickerCoffee());
	}
}
