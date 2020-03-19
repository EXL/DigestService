package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.util.system.SystemReport;

@Component
public class HostIpCommand extends MessageAdminAbility {
	private final BotConfiguration config;
	private final RestHelper rest;
	private final SystemReport report;

	public HostIpCommand(BotConfiguration config, RestHelper rest, SystemReport report) {
		this.config = config;
		this.rest = rest;
		this.report = report;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		Answer<String> res = rest.getRestResponse(config.getUrlHostIp());
		String info = report.getSystemReportMarkdown();
		String ip = "IP: " + ((res.ok()) ? res.answer() :
			String.format(locale.i18n("bot.error.hostip"), res.error()));

		sender.replyMarkdown(message.chat().id(), message.messageId(),
			String.format(locale.i18n("bot.command.info"), info + "\n" + ip));
	}
}
