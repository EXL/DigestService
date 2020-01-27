package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.keyboard.impl.ChartsKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class ChartsCommand extends BotAbility {
	private final ChartsKeyboard keyboard;
	private final ChartService service;

	public ChartsCommand(ChartsKeyboard keyboard, ChartService service) {
		this.keyboard = keyboard;
		this.service = service;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		sender.replyKeyboard(message.chat().id(), message.messageId(), "Test!", keyboard.getMarkup());
	}
}
