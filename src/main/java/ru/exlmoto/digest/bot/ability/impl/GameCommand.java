package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class GameCommand extends BotAbility {
	private final BotConfiguration config;
	private final ImageHelper rest;

	public GameCommand(BotConfiguration config, ImageHelper rest) {
		this.config = config;
		this.rest = rest;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		Answer<String> res = rest.getImageByLink(config.getUrlGame());
		if (res.ok()) {
			sender.replyPhoto(message.chat().id(), message.messageId(), res.answer(),
				locale.i18n("bot.command.game"));
		} else {
			sender.replyMessage(message.chat().id(), message.messageId(),
				String.format(locale.i18n("bot.error.game"), res.error()));
		}
	}
}
