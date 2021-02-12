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

package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.RateJsonGenerator;
import ru.exlmoto.digest.exchange.generator.RateTgMarkdownGenerator;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.exchange.manager.ExchangeManager;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Service
public class ExchangeService {
	private final ExchangeManager manager;
	private final RateTgMarkdownGenerator markdownGenerator;
	private final RateJsonGenerator jsonGenerator;
	private final LocaleHelper locale;

	public ExchangeService(ExchangeManager manager,
	                       RateTgMarkdownGenerator markdownGenerator,
	                       RateJsonGenerator jsonGenerator,
	                       LocaleHelper locale) {
		this.manager = manager;
		this.markdownGenerator = markdownGenerator;
		this.jsonGenerator = jsonGenerator;
		this.locale = locale;
	}

	@Scheduled(cron = "${cron.exchange.rates.update}")
	public void updateAllRates() {
		manager.commitAllRates();
	}

	public String markdownReport(String key) {
		return markdownGenerator.rateReportByKey(key);
	}

	public String jsonReport(String key) {
		return jsonGenerator.getJsonReport(key);
	}

	public String buttonLabel(String key) {
		switch (ExchangeKey.checkExchangeKey(key)) {
			default:
			case bank_ru: return locale.i18n("exchange.bank.ru.button");
			case bank_ua: return locale.i18n("exchange.bank.ua.button");
			case bank_by: return locale.i18n("exchange.bank.by.button");
			case bank_kz: return locale.i18n("exchange.bank.kz.button");
			case bitcoin: return locale.i18n("exchange.bitcoin.button");
			case metal_ru: return locale.i18n("exchange.metal.ru.button");
		}
	}
}
