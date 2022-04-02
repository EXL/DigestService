/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.exchange.parser.additional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.ExchangeRateRbcEntity;
import ru.exlmoto.digest.exchange.parser.GeneralParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.exchange.key.ExchangeRbcKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.USD_CASH_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.EUR_CASH_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.USD_EXCH_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.EUR_EXCH_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.USD_CBRF_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.EUR_CBRF_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.EUR_USD_ID;
import static ru.exlmoto.digest.exchange.key.ExchangeRbcKey.BTC_USD_ID;

public class RateRbcParser extends GeneralParser {
	private final Logger log = LoggerFactory.getLogger(RateRbcParser.class);

	private static class Quotes {
		public String name = null;
		public String date = null;
		public String sell = null;
		public String purc = null;
		public String diff = null;
	}

	private final Map<String, Quotes> valuesMap = new LinkedHashMap<>();

	private Quotes getCashValues(JsonObject object) {
		return getGeneralValues(object, "value1");
	}

	private Quotes getOtherValues(JsonObject object) {
		return getGeneralValues(object, "closevalue");
	}

	private Quotes getGeneralValues(JsonObject object, String sellField) {
		Quotes quotes = new Quotes();
		quotes.name = object.getAsJsonPrimitive("ticker").getAsString();
		JsonObject prepared = object.getAsJsonObject("prepared");
		quotes.date = filterCommas(filterSpaces(prepared.getAsJsonPrimitive("maxDealDate").getAsString()));
		quotes.sell = filterCommas(filterSpaces(prepared.getAsJsonPrimitive(sellField).getAsString()));
		quotes.purc = filterCommas(filterSpaces(prepared.getAsJsonPrimitive("value2").getAsString()));
		quotes.diff = filterCommas(filterSpaces(prepared.getAsJsonPrimitive("change").getAsString()));
		return quotes;
	}

	public Optional<JsonObject> getElementById(JsonArray array, String id) {
		for (JsonElement element : array) {
			JsonObject item = element.getAsJsonObject().getAsJsonObject("item");
			if (item.getAsJsonPrimitive("ticker_id").getAsString().equals(id)) {
				return Optional.of(item);
			}
		}
		return Optional.empty();
	}

	public boolean parse(String content) {
		if (StringUtils.hasText(content)) {
			try {
				JsonObject document = JsonParser.parseString(content).getAsJsonObject();
				JsonArray arr = document.getAsJsonArray("shared_key_indicators");
				if (!arr.isEmpty()) {
					getElementById(arr, USD_CASH_ID).ifPresent(e -> valuesMap.put(USD_CASH_ID, getCashValues(e)));
					getElementById(arr, EUR_CASH_ID).ifPresent(e -> valuesMap.put(EUR_CASH_ID, getCashValues(e)));
					getElementById(arr, USD_EXCH_ID).ifPresent(e -> valuesMap.put(USD_EXCH_ID, getOtherValues(e)));
					getElementById(arr, EUR_EXCH_ID).ifPresent(e -> valuesMap.put(EUR_EXCH_ID, getOtherValues(e)));
					getElementById(arr, USD_CBRF_ID).ifPresent(e -> valuesMap.put(USD_CBRF_ID, getOtherValues(e)));
					getElementById(arr, EUR_CBRF_ID).ifPresent(e -> valuesMap.put(EUR_CBRF_ID, getOtherValues(e)));
					getElementById(arr, EUR_USD_ID).ifPresent(e -> valuesMap.put(EUR_USD_ID, getOtherValues(e)));
					getElementById(arr, BTC_USD_ID).ifPresent(e -> valuesMap.put(BTC_USD_ID, getOtherValues(e)));
					return true;
				}
			} catch (Exception e) {
				log.error("Cannot parse json string.", e);
			}
		}
		return false;
	}

	private void updateEntity(DatabaseService service, String key, Quotes quotes) {
		int id = ExchangeRbcKey.convertRbcIdToDatabaseId(key);
		ExchangeRateRbcEntity entity = service.getRbcQuotes(id).orElse(null);
		if (entity == null) {
			entity = new ExchangeRateRbcEntity();
			entity.setId(id);
			log.warn("Will create new rows in the 'exchange_rate_rbc' table with id: " + id + ".");
		}
		if (StringUtils.hasText(quotes.date)) {
			entity.setDate(quotes.date);
		}
		if (StringUtils.hasText(quotes.purc)) {
			entity.setPurchase(quotes.purc);
		}
		if (StringUtils.hasText(quotes.sell)) {
			entity.setSale(quotes.sell);
		}
		if (StringUtils.hasText(quotes.diff)) {
			entity.setDifference(quotes.diff);
		}
		service.saveRbcExchange(entity);
	}

	private void commit(DatabaseService service) {
		logParsedValues();
		valuesMap.forEach((k, v) -> updateEntity(service, k, v));
	}

	public boolean commitRates(String url, DatabaseService service, RestHelper rest) {
		try {
			if (parse(rest.getRestResponse(url).answer())) {
				commit(service);
				return true;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save object to database.", dae);
		}
		return false;
	}

	public void logParsedValues() {
		log.info("==> Using RBC");
		valuesMap.forEach((k, v) -> log.info(logHelper(v.name, v.date, v.sell, v.purc, v.diff)));
	}

	private String logHelper(String name, String date, String sell, String purc, String diff) {
		return "===> " + name + ", Date: " + date + ", Sell: " + sell + ", Purchase: " + purc + ", Difference: " + diff;
	}
}
