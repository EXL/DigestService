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

package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankRuParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBankRu().orElse(null);
	}

	public boolean commitRatesMirror(String url, DatabaseService service, RestHelper rest) {
		mirror = true;
		return commitRates(url, service, rest);
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_RU_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setUah(uah);
		entity.setByn(byn);
		entity.setKzt(kzt);
	}

	@Override
	protected void setPrevValuesFirstAux(ExchangeRateEntity entity) {
		entity.setPrevUah(uah);
		entity.setPrevByn(byn);
		entity.setPrevKzt(kzt);
	}

	@Override
	protected void setPrevValuesAux(ExchangeRateEntity entity) {
		entity.setPrevUah(updatePrevValue(entity.getUah(), uah, entity.getPrevUah()));
		entity.setPrevByn(updatePrevValue(entity.getByn(), byn, entity.getPrevByn()));
		entity.setPrevKzt(updatePrevValue(entity.getKzt(), kzt, entity.getPrevKzt()));
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "R01235");
		eur = parseValue(document, "R01239");
		kzt = parseValue(document, "R01335");
		byn = parseValue(document, "R01090B");
		uah = parseValue(document, "R01720");
		cny = parseValue(document, "R01375");
		gbp = parseValue(document, "R01035");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Valute[ID=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Nominal").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Value").text()));
		return value.divide(nominal, RoundingMode.FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("ValCurs").attr("Date");
	}
}
