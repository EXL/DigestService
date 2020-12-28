/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.impl.RateKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.service.DatabaseService;

@Component
public class RateWorker {
	private final Logger log = LoggerFactory.getLogger(RateWorker.class);

	private final BotSender botSender;
	private final RateKeyboard rateKeyboard;
	private final ExchangeService exchangeService;
	private final DatabaseService databaseService;

	public RateWorker(BotSender botSender,
	                  RateKeyboard rateKeyboard,
	                  ExchangeService exchangeService,
	                  DatabaseService databaseService) {
		this.botSender = botSender;
		this.rateKeyboard = rateKeyboard;
		this.exchangeService = exchangeService;
		this.databaseService = databaseService;
	}

	@Scheduled(cron = "${cron.exchange.rates.send}")
	public void sendExchangeRatesToSubs() {
		databaseService.getAllRateSubs().forEach(sub -> {
			log.info(String.format("=> Send Exchange Rates to chat '%s' with '%d' id.",
				sub.getName(), sub.getSubscription()));

			botSender.sendMarkdown(sub.getSubscription(),
				exchangeService.markdownReport(ExchangeKey.bank_ru.name()), rateKeyboard.getMarkup());
		});
	}
}
