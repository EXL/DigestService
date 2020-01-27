package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.keyboard.impl.ChartKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class ChartCommand extends BotAbility {
	private final ChartKeyboard chartKeyboard;
	private final ChartService chartService;

	public ChartCommand(ChartKeyboard chartKeyboard, ChartService chartService) {
		this.chartKeyboard = chartKeyboard;
		this.chartService = chartService;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		sender.replyKeyboard(message.chat().id(), message.messageId(),
			locale.i18n("bot.command.charts") + "\n\n" + chartService.markdownDescriptions(),
			chartKeyboard.getMarkup());
	}
}
