package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class HostIpCommand extends MessageAdminAbility {
	private final BotConfiguration config;
	private final RestHelper rest;

	public HostIpCommand(BotConfiguration config, RestHelper rest) {
		this.config = config;
		this.rest = rest;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		Answer<String> res = rest.getRestResponse(config.getUrlHostIp());
		sender.replyMessage(message.chat().id(), message.messageId(),
			(res.ok()) ?
				String.format(locale.i18n("bot.command.hostip"), res.answer()) :
				String.format(locale.i18n("bot.error.hostip"), res.error())
		);
	}
}
