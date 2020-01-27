package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.util.ExchangeKeys;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class RateCommand extends BotAbility {
	private final ExchangeService service;

	public RateCommand(ExchangeService service) {
		this.service = service;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		sender.replyKeyboard(message.chat().id(), message.messageId(),
			service.markdownReport(ExchangeKeys.BANK_RU), null);
	}
}
