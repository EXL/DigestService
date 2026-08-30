/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.ExchangeRateErapiEntity;
import ru.exlmoto.digest.exchange.parser.GeneralParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class RateErapiParser extends GeneralParser {
	private static final String QUOTE_USD_RUB = "USD/RUB";
	private static final String QUOTE_EUR_RUB = "EUR/RUB";
	private static final String QUOTE_EUR_USD = "EUR/USD";

	private final Logger log = LoggerFactory.getLogger(RateErapiParser.class);

	private static class Quotes {
		public String name = null;
		public String date = null;
		public String sell = null;
	}

	private final Map<String, Quotes> valuesMap = new LinkedHashMap<>();

	public boolean parse(String content) {
		valuesMap.clear();
		if (!StringUtils.hasText(content)) {
			return false;
		}

		try {
			JsonObject document = JsonParser.parseString(content).getAsJsonObject();
			if (!document.has("rates") || !document.has("result") || !"success".equalsIgnoreCase(document.get("result").getAsString())) {
				return false;
			}

			JsonObject rates = document.getAsJsonObject("rates");
			BigDecimal rubRate = getDecimalValue(rates, "RUB");
			BigDecimal eurRate = getDecimalValue(rates, "EUR");
			if (rubRate == null || eurRate == null) {
				return false;
			}

			String dateStr = getStringValue(document, "time_last_update_utc");
			String formattedDate = formatDate(dateStr);

			putQuote(QUOTE_USD_RUB, formattedDate, rubRate, "%.2f");
			putQuote(QUOTE_EUR_RUB, formattedDate, rubRate.divide(eurRate, 8, RoundingMode.HALF_UP), "%.2f");
			putQuote(QUOTE_EUR_USD, formattedDate, BigDecimal.ONE.divide(eurRate, 8, RoundingMode.HALF_UP), "%.5f");

			return !valuesMap.isEmpty();
		} catch (Exception e) {
			log.error("Cannot parse ER API JSON string.", e);
		}
		return false;
	}

	private void putQuote(String key, String dateStr, BigDecimal value, String format) {
		Quotes quotes = new Quotes();
		quotes.name = key;
		quotes.date = StringUtils.hasText(dateStr) ? dateStr : "";
		quotes.sell = String.format(Locale.ROOT, format, value.doubleValue());
		valuesMap.put(key, quotes);
	}

	private BigDecimal getDecimalValue(JsonObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
			try {
				return BigDecimal.valueOf(jsonObject.get(key).getAsDouble());
			} catch (Exception ignored) { }
		}
		return null;
	}

	private String getStringValue(JsonObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
			return jsonObject.get(key).getAsString();
		}
		return "";
	}

	@Override
	protected String formatDate(String dateStr) {
		if (StringUtils.hasText(dateStr)) {
			try {
				SimpleDateFormat input = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
				Date date = input.parse(dateStr);
				if (date != null) {
					return new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(date);
				}
			} catch (ParseException ignored) {
				// keep the original string if the API response format is unexpected
			}
		}
		return dateStr;
	}

	private int getDatabaseId(String key) {
		switch (key) {
			case QUOTE_USD_RUB:
				return ExchangeRateErapiEntity.ERAPI_ROW_USD_EXCH;
			case QUOTE_EUR_RUB:
				return ExchangeRateErapiEntity.ERAPI_ROW_EUR_EXCH;
			case QUOTE_EUR_USD:
				return ExchangeRateErapiEntity.ERAPI_ROW_EUR_USD;
			default:
				throw new IllegalArgumentException("Unknown ER API quote: " + key);
		}
	}

	private void updateEntity(DatabaseService service, String key, Quotes quotes) {
		int id = getDatabaseId(key);
		ExchangeRateErapiEntity entity = service.getErapiQuotes(id).orElse(null);
		if (entity == null) {
			entity = new ExchangeRateErapiEntity();
			entity.setId(id);
			log.warn("Will create new rows in the 'exchange_rate_erapi' table with id: " + id + ".");
		}
		if (StringUtils.hasText(quotes.date)) {
			entity.setDate(quotes.date);
		}

		if (StringUtils.hasText(quotes.sell)) {
			String currentSale = entity.getSale();

			if (StringUtils.hasText(currentSale)) {
				if (!currentSale.equals(quotes.sell)) {
					entity.setPrev(currentSale);
				}
			} else {
				entity.setPrev(quotes.sell);
			}

			entity.setSale(quotes.sell);
		}

		service.saveErapiExchange(entity);
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
		log.info("==> Using ER API");
		valuesMap.forEach((k, v) -> log.info(logHelper(v.name, v.date, v.sell)));
	}


	private String logHelper(String name, String date, String sell) {
		return "===> " + name + ", Date: " + date + ", Sell: " + sell;
	}
}
