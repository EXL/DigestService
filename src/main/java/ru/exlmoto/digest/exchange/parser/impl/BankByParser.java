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

package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.DatabaseService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankByParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBankBy().orElse(null);
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "145");
		eur = parseValue(document, "292");
		kzt = parseValue(document, "301");
		rub = parseValue(document, "298");
		uah = parseValue(document, "290");
		cny = parseValue(document, "304");
		gbp = parseValue(document, "143");
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_BY_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setRub(rub);
		entity.setUah(uah);
		entity.setKzt(kzt);
	}

	@Override
	protected void setPrevValuesFirstAux(ExchangeRateEntity entity) {
		entity.setPrevRub(rub);
		entity.setPrevUah(uah);
		entity.setPrevKzt(kzt);
	}

	@Override
	protected void setPrevValuesAux(ExchangeRateEntity entity) {
		entity.setPrevRub(updatePrevValue(entity.getRub(), rub, entity.getPrevRub()));
		entity.setPrevUah(updatePrevValue(entity.getUah(), uah, entity.getPrevUah()));
		entity.setPrevKzt(updatePrevValue(entity.getKzt(), kzt, entity.getPrevKzt()));
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Currency[Id=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Scale").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Rate").text()));
		return value.divide(nominal, 8, RoundingMode.HALF_UP);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("DailyExRates").attr("Date");
	}
}
