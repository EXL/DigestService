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
import java.util.Locale;
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
	}

	private final Map<String, Quotes> valuesMap = new LinkedHashMap<>();

	private Map<String, Integer> getColumnIndexes(JsonArray columnsArray) {
		Map<String, Integer> indexes = new HashMap<>();
		if (columnsArray != null) {
			for (int i = 0; i < columnsArray.size(); i++) {
				indexes.put(columnsArray.get(i).getAsString(), i);
			}
		}
		return indexes;
	}

	public boolean parse(String content) {
		if (StringUtils.hasText(content)) {
			try {
				JsonObject document = JsonParser.parseString(content).getAsJsonObject();

				JsonObject securities = document.has("securities") ? document.getAsJsonObject("securities") : null;
				JsonObject marketData = document.has("marketdata") ? document.getAsJsonObject("marketdata") : null;

				if (securities == null && marketData == null) {
					return false;
				}

				Map<String, Double> prevPrices = new HashMap<>();
				Map<String, String> secDates = new HashMap<>();

				if (securities != null) {
					JsonArray secCols = securities.getAsJsonArray("columns");
					JsonArray secData = securities.getAsJsonArray("data");

					if (secCols != null && secData != null) {
						Map<String, Integer> secIdx = getColumnIndexes(secCols);

						int secIdIdx = secIdx.getOrDefault("SECID", -1);
						int prevPriceIdx = secIdx.getOrDefault("PREVPRICE", -1);
						int tradeDateIdx = secIdx.getOrDefault("TRADEDATE", -1);

						for (JsonElement rowElement : secData) {
							JsonArray row = rowElement.getAsJsonArray();
							String secId = getStringValue(row, secIdIdx);

							if (TARGET_TICKERS.contains(secId)) {
								Double prevPrice = getDoubleValue(row, prevPriceIdx);
								String tradeDate = getStringValue(row, tradeDateIdx);

								if (prevPrice != null && prevPrice > 0) {
									prevPrices.put(secId, prevPrice);
								}
								if (StringUtils.hasText(tradeDate)) {
									secDates.put(secId, tradeDate);
								}
							}
						}
					}
				}

				if (marketData != null) {
					JsonArray mdCols = marketData.getAsJsonArray("columns");
					JsonArray mdData = marketData.getAsJsonArray("data");

					if (mdCols != null && mdData != null) {
						Map<String, Integer> mdIdx = getColumnIndexes(mdCols);

						int secIdIdx = mdIdx.getOrDefault("SECID", -1);
						int lastIdx = mdIdx.getOrDefault("LAST", -1);
						int closingPriceIdx = mdIdx.getOrDefault("CLOSINGPRICE", -1);
						int dateIdx = mdIdx.getOrDefault("TRADEDATE", -1);
						int sysTimeIdx = mdIdx.getOrDefault("SYSTIME", -1);

						for (JsonElement rowElement : mdData) {
							JsonArray row = rowElement.getAsJsonArray();
							String secId = getStringValue(row, secIdIdx);

							if (TARGET_TICKERS.contains(secId)) {
								Quotes quotes = new Quotes();
								quotes.name = secId;

								// 1. Parsing trade date and formatting it to DD.MM.YYYY.
								String dateStr = getStringValue(row, dateIdx);
								if (!StringUtils.hasText(dateStr)) {
									dateStr = secDates.getOrDefault(secId, "");
								}
								if (!StringUtils.hasText(dateStr)) {
									String sysTime = getStringValue(row, sysTimeIdx);
									if (StringUtils.hasText(sysTime) && sysTime.contains(" ")) {
										dateStr = sysTime.split(" ")[0];
									}
								}
								quotes.date = formatDate(filterCommas(filterSpaces(dateStr)));

								// 2. Extracting price: LAST -> CLOSINGPRICE -> PREVPRICE.
								Double lastVal = getDoubleValue(row, lastIdx);
								Double closingVal = getDoubleValue(row, closingPriceIdx);
								Double prevPriceVal = prevPrices.get(secId);

								double finalPrice = 0.0;
								if (lastVal != null && lastVal > 0) {
									finalPrice = lastVal;
								} else if (closingVal != null && closingVal > 0) {
									finalPrice = closingVal;
								} else if (prevPriceVal != null && prevPriceVal > 0) {
									finalPrice = prevPriceVal;
								}

								// Keep 5 precision places for EUR/USD pair, 2 for others.
								boolean isEurUsd = ExchangeMoexKey.EUR_USD_ID.equals(secId);
								String priceFormat = isEurUsd ? "%.5f" : "%.2f";
								quotes.sell = (finalPrice > 0) ? String.format(Locale.ROOT, priceFormat, finalPrice) : "0.00";

								valuesMap.put(secId, quotes);
							}
						}
					}
				}
				return !valuesMap.isEmpty();
			} catch (Exception e) {
				log.error("Cannot parse MOEX JSON string.", e);
			}
		}
		return false;
	}

	@Override
	protected String formatDate(String dateStr) {
		if (StringUtils.hasText(dateStr) && dateStr.contains("-")) {
			String[] parts = dateStr.split("-");
			if (parts.length == 3 && parts[0].length() == 4) {
				return parts[2] + "." + parts[1] + "." + parts[0];
			}
		}
		return dateStr;
	}

	private String getStringValue(JsonArray row, int index) {
		if (index != -1 && index < row.size() && !row.get(index).isJsonNull()) {
			return row.get(index).getAsString();
		}
		return "";
	}

	private Double getDoubleValue(JsonArray row, int index) {
		if (index != -1 && index < row.size() && !row.get(index).isJsonNull()) {
			try {
				return row.get(index).getAsDouble();
			} catch (Exception ignored) { }
		}
		return null;
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
		valuesMap.forEach((k, v) -> log.info(logHelper(v.name, v.date, v.sell)));
	}

	private String logHelper(String name, String date, String sell) {
		return "===> " + name + ", Date: " + date + ", Sell: " + sell;
	}
}
