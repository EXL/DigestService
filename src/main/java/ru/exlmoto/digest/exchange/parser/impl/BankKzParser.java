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

public class BankKzParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBankKz().orElse(null);
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_KZ_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setRub(rub);
		entity.setUah(uah);
		entity.setByn(byn);
	}

	@Override
	protected void setPrevValuesFirstAux(ExchangeRateEntity entity) {
		entity.setPrevRub(rub);
		entity.setPrevUah(uah);
		entity.setPrevByn(byn);
	}

	@Override
	protected void setPrevValuesAux(ExchangeRateEntity entity) {
		entity.setPrevRub(updatePrevValue(entity.getRub(), rub, entity.getPrevRub()));
		entity.setPrevUah(updatePrevValue(entity.getUah(), uah, entity.getPrevUah()));
		entity.setPrevByn(updatePrevValue(entity.getByn(), byn, entity.getPrevByn()));
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "USD");
		eur = parseValue(document, "EUR");
		rub = parseValue(document, "RUB");
		byn = parseValue(document, "BYN");
		uah = parseValue(document, "UAH");
		cny = parseValue(document, "CNY");
		gbp = parseValue(document, "GBP");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("title:contains(" + valueId + ")").parent();
		BigDecimal quant = new BigDecimal(filterCommas(element.selectFirst("quant").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("description").text()));
		return value.divide(quant, 8, RoundingMode.HALF_UP);
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("title:contains(USD)").parent();
		return element.selectFirst("pubDate").text();
	}
}
