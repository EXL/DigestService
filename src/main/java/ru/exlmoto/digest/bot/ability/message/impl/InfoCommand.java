package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.system.SystemReport;

@Component
public class InfoCommand extends MessageAdminAbility {
	private final SystemReport report;

	public InfoCommand(SystemReport report) {
		this.report = report;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		sender.replyMarkdown(message.chat().id(), message.messageId(),
			String.format(locale.i18n("bot.command.info"), report.getSystemReportMarkdown()));
	}
}
