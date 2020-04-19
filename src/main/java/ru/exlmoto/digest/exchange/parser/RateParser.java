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

package ru.exlmoto.digest.exchange.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public abstract class RateParser {
	private final Logger log = LoggerFactory.getLogger(RateParser.class);

	protected String date = null;
	protected boolean mirror = false;

	public boolean parse(String content) {
		if (!StringUtils.isEmpty(content)) {
			try {
				return parseDocument(Jsoup.parse(content));
			} catch (Exception e) {
				log.error(String.format("Error while parsing document. Chunk: '%s'.", chopContent(content)), e);
				/*
				 * Uncomment the line below to be able to get values from part of the document. It may be dangerous
				 * because some services give you a chunk of data, while on the mirror you can get all the values.
				 */
				// return checkParsedValues();
			}
		}
		return false;
	}

	private String chopContent(String content) {
		int SMALL_STRING_SIZE = 50;
		if (content.length() < SMALL_STRING_SIZE) {
			return filterLines(content);
		}
		return filterLines(content.substring(0, SMALL_STRING_SIZE));
	}

	protected BigDecimal parseValue(Document document, String valueId) {
		try {
			return parseValueAux(document, valueId);
		} catch (NumberFormatException nfe) {
			log.error("Error parsing some value from document.", nfe);
			return null;
		}
	}

	private boolean parseDocument(Document document) {
		Assert.notNull(document, "Document must not be null.");
		date = parseDate(document);
		parseDocumentAux(document);
		return checkParsedValues();
	}

	public boolean commitRates(String url, DatabaseService service, RestHelper rest) {
		try {
			if (parse(rest.getRestResponse(url).answer())) {
				commit(getEntity(service), service);
				return true;
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save object to database.", dae);
		}
		return false;
	}

	public void commit(ExchangeRateEntity entity, DatabaseService service) {
		logRates();
		BigDecimal prevValue = null;
		if (entity != null) {
			if (entity.getId() == ExchangeRateEntity.METAL_RU_ROW) {
				prevValue = entity.getGold();
			} else {
				prevValue = entity.getUsd();
			}
		} else {
			entity = new ExchangeRateEntity();
			entity.setId(entityId());
		}

		entity.setDate(date);
		setPrevGeneralValues(entity, prevValue);
		commitGeneralValues(entity);

		service.saveExchange(entity);
	}

	private void logRates() {
		String message = "==> Using ";
		if (mirror) {
			message += "mirror ";
		}
		message += getClass().getSimpleName() + ".";
		log.info(message);
		logParsedValues();
	}

	protected abstract ExchangeRateEntity getEntity(DatabaseService service);

	protected abstract void setPrevGeneralValues(ExchangeRateEntity entity, BigDecimal prevValue);

	protected abstract int entityId();

	protected abstract void commitGeneralValues(ExchangeRateEntity entity);

	protected abstract void parseDocumentAux(Document document);

	protected abstract boolean checkParsedValues();

	protected abstract BigDecimal parseValueAux(Document document, String valueId);

	protected abstract String parseDate(Document document);

	public abstract void logParsedValues();

	private String filterLines(String value) {
		return value.replaceAll("[\\t\\n\\r]+"," ").trim();
	}

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected String filterSpaces(String value) {
		return value.replaceAll(" ", "");
	}
}
