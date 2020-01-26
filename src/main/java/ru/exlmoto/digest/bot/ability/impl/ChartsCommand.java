package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.keyboard.impl.ChartsKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class ChartsCommand extends BotAbility {
	private final ChartsKeyboard chartsKeyboard;

	public ChartsCommand(ChartsKeyboard chartsKeyboard) {
		this.chartsKeyboard = chartsKeyboard;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		sender.replyKeyboard(message.chat().id(), message.messageId(), "Test!", chartsKeyboard.getMarkup());
	}
}
