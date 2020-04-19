/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

import java.math.BigDecimal;

public abstract class BankParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(BankParser.class);

	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal rub = null;
	protected BigDecimal cny = null;
	protected BigDecimal gbp = null;

	@Override
	protected void setPrevGeneralValues(ExchangeRateEntity entity, BigDecimal prevValue) {
		if (prevValue != null) {
			entity.setPrevUsd(entity.getUsd());
			entity.setPrevEur(entity.getEur());
			entity.setPrevGbp(entity.getGbp());
			entity.setPrevCny(entity.getCny());
			setPrevValuesAux(entity);
		} else {
			entity.setPrevUsd(usd);
			entity.setPrevEur(eur);
			entity.setPrevCny(cny);
			entity.setPrevGbp(gbp);
			setPrevValuesFirstAux(entity);
		}
	}

	@Override
	protected void commitGeneralValues(ExchangeRateEntity entity) {
		entity.setUsd(usd);
		entity.setEur(eur);
		entity.setGbp(gbp);
		entity.setCny(cny);

		commitAux(entity);
	}

	@Override
	protected boolean checkParsedValues() {
		return usd != null && eur != null && cny != null && gbp != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, UAH: %s, CNY: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, uah, cny, gbp
			)
		);
	}

	protected abstract void commitAux(ExchangeRateEntity entity);

	protected abstract void setPrevValuesFirstAux(ExchangeRateEntity entity);

	protected abstract void setPrevValuesAux(ExchangeRateEntity entity);
}
