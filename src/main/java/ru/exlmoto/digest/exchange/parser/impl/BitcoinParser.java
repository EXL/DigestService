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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.nodes.Document;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.math.BigDecimal;

public class BitcoinParser extends BankParser {
	protected JsonArray rateArray = null;

	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBitcoin().orElse(null);
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BITCOIN_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setRub(rub);
		entity.setUah(uah);
		entity.setByn(byn);
		entity.setKzt(kzt);
	}

	@Override
	protected void setPrevValuesFirstAux(ExchangeRateEntity entity) {
		entity.setPrevRub(rub);
		entity.setPrevUah(uah);
		entity.setPrevByn(byn);
		entity.setPrevKzt(kzt);
	}

	@Override
	protected void setPrevValuesAux(ExchangeRateEntity entity) {
		entity.setPrevRub(updatePrevValue(entity.getRub(), rub, entity.getPrevRub()));
		entity.setPrevUah(updatePrevValue(entity.getUah(), uah, entity.getPrevUah()));
		entity.setPrevByn(updatePrevValue(entity.getByn(), byn, entity.getPrevByn()));
		entity.setPrevKzt(updatePrevValue(entity.getKzt(), kzt, entity.getPrevKzt()));
	}

	@Override
	protected void parseDocumentAux(Document document) {
		rateArray = JsonParser.parseString(document.text()).getAsJsonArray();

		usd = parseValue(document, "USD");
		eur = parseValue(document, "EUR");
		gbp = parseValue(document, "GBP");
		cny = parseValue(document, "CNY");
		rub = parseValue(document, "RUB");
		uah = parseValue(document, "UAH");
		byn = parseValue(document, "BYN");
		kzt = parseValue(document, "KZT");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		for (JsonElement rate : rateArray) {
			JsonObject object = rate.getAsJsonObject();
			if (object.getAsJsonPrimitive("code").getAsString().equals(valueId)) {
				return new BigDecimal(object.getAsJsonPrimitive("rate").getAsString());
			}
		}
		throw new NumberFormatException(String.format("Cannot get '%s' Bitcoin rate.", valueId));
	}

	@Override
	protected String parseDate(Document document) {
		return FilterHelper.getDateFromTimeStamp("dd.MM.yyyy", FilterHelper.getCurrentUnixTime());
	}
}
