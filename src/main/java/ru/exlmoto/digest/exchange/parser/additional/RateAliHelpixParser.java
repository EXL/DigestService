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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import ru.exlmoto.digest.entity.ExchangeRateAliEntity;
import ru.exlmoto.digest.exchange.key.ExchangeAliKey;

public class RateAliHelpixParser extends RateAliParser {
	private final Logger log = LoggerFactory.getLogger(RateAliHelpixParser.class);

	public RateAliHelpixParser() {
		super();
	}

	@Override
	public boolean parse(String content) {
		try {
			return parseDocument(Jsoup.parse(content));
		} catch (Exception e) {
			log.error(String.format("Error while parsing Helpix document. Chunk: '%s'.", chopContent(content)), e);
		}
		return false;
	}

	private boolean parseDocument(Document document) {
		Assert.notNull(document, "Helpix document must not be null.");
		Elements b_tabcurr = document.getElementsByClass("b-tabcurr");
		if (!b_tabcurr.isEmpty()) {
			Element table = b_tabcurr.first();
			Assert.notNull(table, "Table 'b-tabcurr' is null.");
			Elements trs = table.getElementsByTag("tr");

			// Drop first line with table header.
			for (int i = 1; i <= ExchangeRateAliEntity.MAX_LAST_ROWS; ++i) {
				final int DATE_INDEX = 0;
				final int CBRF_INDEX = 1;
				final int ALIE_INDEX = 2;
				Elements tds = trs.get(i).getElementsByTag("td");
				lastRowsArray.add(
					new ExchangeAliKey(
						formatDate(tds.get(DATE_INDEX).text()),
						tds.get(CBRF_INDEX).text(),
						tds.get(ALIE_INDEX).text()
					)
				);
			}
		} else {
			log.error("Cannot find 'b-tabcurr' class.");
			return false;
		}
		return true;
	}
}
