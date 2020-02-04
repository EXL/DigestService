package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.ability.keyboard.impl.ChartKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class ChartCommand extends MessageAbility {
	private final ChartKeyboard chartKeyboard;
	private final ChartService chartService;

	public ChartCommand(ChartKeyboard chartKeyboard, ChartService chartService) {
		this.chartKeyboard = chartKeyboard;
		this.chartService = chartService;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		sender.replyMarkdown(message.chat().id(), message.messageId(),
			locale.i18n("bot.command.charts") + "\n\n" + chartService.markdownDescriptions(),
			chartKeyboard.getMarkup());
	}
}
