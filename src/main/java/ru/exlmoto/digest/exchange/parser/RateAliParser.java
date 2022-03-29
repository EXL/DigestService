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

package ru.exlmoto.digest.exchange.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.ExchangeRateAliEntity;
import ru.exlmoto.digest.exchange.key.ExchangeAliKey;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;

public class RateAliParser extends GeneralParser {
	private final Logger log = LoggerFactory.getLogger(RateAliParser.class);

	private List<ExchangeAliKey> lastRowsArray;

	public RateAliParser() {
		lastRowsArray = new ArrayList<ExchangeAliKey>();
	}

	private boolean parseArrays(JsonArray labels, JsonArray stockexchange, JsonArray aliexpress) {
		if (!labels.isEmpty() && !stockexchange.isEmpty() && !aliexpress.isEmpty()) {
			final int size = labels.size();
			for (int i = size - 1; i >= size - ExchangeRateAliEntity.MAX_LAST_ROWS; --i) {
				lastRowsArray.add(
					new ExchangeAliKey(
						formatDate(labels.get(i).getAsJsonPrimitive().getAsString()),
						removeLastCharacters(stockexchange.get(i).getAsJsonPrimitive().getAsString(), 2),
						removeLastCharacters(aliexpress.get(i).getAsJsonPrimitive().getAsString(), 2)
					)
				);
			}
			return true;
		}
		return false;
	}

	public boolean parse(String content) {
		if (StringUtils.hasLength(content)) {
			try {
				JsonObject document = JsonParser.parseString​(content).getAsJsonObject();
				JsonObject data = document.getAsJsonObject("data");
				JsonArray labels = data.getAsJsonArray​("labels");
				JsonArray stockexchange = data.getAsJsonArray​("stockexchange");
				JsonArray aliexpress = data.getAsJsonArray​("aliexpress");

				lastRowsArray.clear();

				return parseArrays(labels, stockexchange, aliexpress);
			} catch (Exception e) {
				log.error("Cannot parse json string.", e);
			}
		}
		return false;
	}

	private void commit(DatabaseService service) {
		logParsedValues();
		final int size = lastRowsArray.size();
		for (int i = 0; i < size; ++i) {
			ExchangeRateAliEntity entity = service.getLastAliRow(i + 1).orElse(null);
			if (entity == null) {
				entity = new ExchangeRateAliEntity();
				entity.setId(i + 1);
				log.warn("Will create new rows in the 'exchange_rate_aliexpress' table with id: " + (i + 1) + ".");
			}
			entity.setDate(lastRowsArray.get(i).getDate());
			entity.setValue(lastRowsArray.get(i).getAliexpress());
			entity.setExchange(lastRowsArray.get(i).getExchange());
			entity.setDifference("-");
			service.saveAliExchange(entity);
		}
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
		log.info("==> Using AliExpress");
		for (ExchangeAliKey row : lastRowsArray) {
			log.info(
				"===> Date: " + row.getDate() +
				", Exchange: " + row.getExchange() +
				", Aliexpress: " + row.getAliexpress()
			);
		}
	}
}
