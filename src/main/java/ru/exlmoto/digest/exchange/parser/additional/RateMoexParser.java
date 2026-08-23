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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.ExchangeRateMoexEntity;
import ru.exlmoto.digest.exchange.key.ExchangeMoexKey;
import ru.exlmoto.digest.exchange.parser.GeneralParser;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RateMoexParser extends GeneralParser {
	private final Logger log = LoggerFactory.getLogger(RateMoexParser.class);

	private static final List<String> TARGET_TICKERS = List.of(
		ExchangeMoexKey.USD_EXCH_ID, ExchangeMoexKey.EUR_EXCH_ID, ExchangeMoexKey.EUR_USD_ID
	);

	private static class Quotes {
		public String name = null;
		public String date = null;
		public String sell = null;
		public String diff = null;
	}

	private final Map<String, Quotes> valuesMap = new LinkedHashMap<>();

	private Map<String, Integer> getColumnIndexes(JsonArray columnsArray) {
		Map<String, Integer> indexes = new HashMap<>();
		for (int i = 0; i < columnsArray.size(); i++) {
			indexes.put(columnsArray.get(i).getAsString(), i);
		}
		return indexes;
	}

	public boolean parse(String content) {
		if (StringUtils.hasText(content)) {
			try {
				JsonObject document = JsonParser.parseString(content).getAsJsonObject();
				if (!document.has("marketdata")) {
					return false;
				}

				JsonObject marketData = document.getAsJsonObject("marketdata");
				JsonArray columns = marketData.getAsJsonArray("columns");
				JsonArray dataArray = marketData.getAsJsonArray("data");

				if (columns == null || dataArray == null) {
					return false;
				}

				Map<String, Integer> colIdx = getColumnIndexes(columns);

				int secIdIdx = colIdx.getOrDefault("SECID", -1);
				int lastIdx = colIdx.getOrDefault("LAST", -1);
				int changeIdx = colIdx.getOrDefault("CHANGE", -1);
				int prevPriceIdx = colIdx.getOrDefault("LASTTOPREVPRICE", -1);
				int timeIdx = colIdx.getOrDefault("UPDATETIME", -1);
				int dateIdx = colIdx.getOrDefault("TRADEDATE", -1);

				if (secIdIdx == -1) {
					return false;
				}

				for (JsonElement rowElement : dataArray) {
					JsonArray row = rowElement.getAsJsonArray();
					String secId = row.get(secIdIdx).isJsonNull() ? "" : row.get(secIdIdx).getAsString();

					if (TARGET_TICKERS.contains(secId)) {
						Quotes quotes = new Quotes();
						quotes.name = secId;

						// Reading date and time, prefer UPDATETIME over TRADEDATE.
						String timeStr = (timeIdx != -1 && !row.get(timeIdx).isJsonNull()) ? row.get(timeIdx).getAsString() : "";
						String dateStr = (dateIdx != -1 && !row.get(dateIdx).isJsonNull()) ? row.get(dateIdx).getAsString() : "";
						quotes.date = filterCommas(filterSpaces(StringUtils.hasText(timeStr) ? timeStr : dateStr));

						// Reading price (LAST, fallback to LASTTOPREVPRICE when the exchange is closed).
						JsonElement lastElem = (lastIdx != -1) ? row.get(lastIdx) : null;
						if (lastElem != null && !lastElem.isJsonNull()) {
							quotes.sell = filterCommas(filterSpaces(lastElem.getAsString()));
						} else if (prevPriceIdx != -1 && !row.get(prevPriceIdx).isJsonNull()) {
							quotes.sell = filterCommas(filterSpaces(row.get(prevPriceIdx).getAsString()));
						}

						// Reading difference (CHANGE).
						JsonElement changeElem = (changeIdx != -1) ? row.get(changeIdx) : null;
						if (changeElem != null && !changeElem.isJsonNull()) {
							quotes.diff = filterCommas(filterSpaces(changeElem.getAsString()));
						} else {
							quotes.diff = "0.00";
						}

						valuesMap.put(secId, quotes);
					}
				}
				return !valuesMap.isEmpty();
			} catch (Exception e) {
				log.error("Cannot parse MOEX JSON string.", e);
			}
		}
		return false;
	}

	private void updateEntity(DatabaseService service, String key, Quotes quotes) {
		int id = ExchangeMoexKey.convertMoexIdToDatabaseId(key);
		ExchangeRateMoexEntity entity = service.getMoexQuotes(id).orElse(null);
		if (entity == null) {
			entity = new ExchangeRateMoexEntity();
			entity.setId(id);
			log.warn("Will create new rows in the 'exchange_rate_moex' table with id: " + id + ".");
		}
		if (StringUtils.hasText(quotes.date)) {
			entity.setDate(quotes.date);
		}
		if (StringUtils.hasText(quotes.sell)) {
			entity.setSale(quotes.sell);
		}
		if (StringUtils.hasText(quotes.diff)) {
			entity.setDifference(quotes.diff);
		}
		service.saveMoexExchange(entity);
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
		log.info("==> Using MOEX");
		valuesMap.forEach((k, v) -> log.info(logHelper(v.name, v.date, v.sell, v.diff)));
	}

	private String logHelper(String name, String date, String sell, String diff) {
		return "===> " + name + ", Date: " + date + ", Sell: " + sell + ", Difference: " + diff;
	}
}
