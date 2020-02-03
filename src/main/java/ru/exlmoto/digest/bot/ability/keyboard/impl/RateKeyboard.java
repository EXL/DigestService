package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

@Component
public class RateKeyboard extends KeyboardAbility {
	private final ExchangeService service;

	private InlineKeyboardMarkup markup = null;

	public RateKeyboard(ExchangeService service) {
		this.service = service;
	}

	@PostConstruct
	private void generateKeyboard() {
		InlineKeyboardButton[] keyboardRow = new InlineKeyboardButton[ExchangeKey.values().length];
		int i = 0;
		for(ExchangeKey key: ExchangeKey.values()) {
			keyboardRow[i++] = new InlineKeyboardButton(service.buttonLabel(key.name()))
				.callbackData(Keyboard.rate.withName() + key);
		}
		markup = new InlineKeyboardMarkup(keyboardRow);
	}

	@Override
	public InlineKeyboardMarkup getMarkup(int page, int totalPages) {
		return markup;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		long chatId = message.chat().id();
		int messageId = message.messageId();

		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		sender.sendCallbackQueryAnswer(callback.id(),
			locale.i18n("bot.inline.selected") + " " + service.buttonLabel(key));

		sender.editMessage(chatId, messageId, service.markdownReport(key), markup);
	}
}
