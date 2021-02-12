/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

@Component
public class RateKeyboard extends KeyboardSimpleAbility {
	private final ExchangeService service;

	private InlineKeyboardMarkup markup = null;

	public RateKeyboard(ExchangeService service) {
		this.service = service;
	}

	@PostConstruct
	private void generateKeyboard() {
		markup = new InlineKeyboardMarkup(
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.bank_ru.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.bank_ru),
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.bank_ua.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.bank_ua),
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.bank_by.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.bank_by)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.bank_kz.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.bank_kz),
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.bitcoin.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.bitcoin),
				new InlineKeyboardButton(service.buttonLabel(ExchangeKey.metal_ru.name()))
					.callbackData(Keyboard.rate.withName() + ExchangeKey.metal_ru)
			}
		);
	}

/*
	@PostConstruct
	private void generateKeyboard() {
		InlineKeyboardButton[] keyboardRow = new InlineKeyboardButton[ExchangeKey.values().length];
		int i = 0;
		ExchangeKey[] exchangeKeys = ExchangeKey.values();
		for (ExchangeKey key : exchangeKeys) {
			keyboardRow[i++] = new InlineKeyboardButton(service.buttonLabel(key.name()))
				.callbackData(Keyboard.rate.withName() + key);
		}
		markup = new InlineKeyboardMarkup(keyboardRow);
	}
*/

	@Override
	public InlineKeyboardMarkup getMarkup() {
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

		sender.editMarkdown(chatId, messageId, service.markdownReport(key), markup);
	}
}
