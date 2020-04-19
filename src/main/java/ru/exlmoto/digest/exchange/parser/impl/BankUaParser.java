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

package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.DatabaseService;

import java.math.BigDecimal;

public class BankUaParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBankUa().orElse(null);
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_UA_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setRub(rub);
		entity.setByn(byn);
		entity.setKzt(kzt);
	}

	@Override
	protected void setPrevValuesFirstAux(ExchangeRateEntity entity) {
		entity.setPrevRub(rub);
		entity.setPrevByn(byn);
		entity.setPrevKzt(kzt);
	}

	@Override
	protected void setPrevValuesAux(ExchangeRateEntity entity) {
		entity.setPrevRub(entity.getRub());
		entity.setPrevByn(entity.getByn());
		entity.setPrevKzt(entity.getKzt());
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "840");
		eur = parseValue(document, "978");
		kzt = parseValue(document, "398");
		byn = parseValue(document, "933");
		rub = parseValue(document, "643");
		cny = parseValue(document, "156");
		gbp = parseValue(document, "826");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("r030:contains(" + valueId + ")").parent();
		return new BigDecimal(filterCommas(element.selectFirst("rate").text()));
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("r030:contains(840)").parent();
		return element.selectFirst("exchangedate").text();
	}
}
