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

import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class GeneralParser {
	protected String chopContent(String content) {
		int SMALL_STRING_SIZE = 50;
		if (content.length() < SMALL_STRING_SIZE) {
			return filterLines(content);
		}
		return filterLines(content.substring(0, SMALL_STRING_SIZE));
	}

	protected String formatDate(String original) {
		final String[] parts = original.substring(5).replaceAll("-", ".").split("\\.");
		return parts[1] + "." + parts[0];
	}

	protected String removeLastCharacters(String original, int n) {
		return original.substring(0, original.length() - n);
	}

	protected String filterLines(String value) {
		return value.replaceAll("[\\t\\n\\r]+"," ").trim();
	}

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected String filterSpaces(String value) {
		return value.replaceAll(" ", "");
	}

	protected BigDecimal updatePrevValue(BigDecimal oldValue, BigDecimal newValue, BigDecimal prevValue) {
		return (oldValue.setScale(8, RoundingMode.HALF_UP)
			.compareTo(newValue.setScale(8, RoundingMode.HALF_UP)) == 0) ? prevValue : oldValue;
	}

	public abstract boolean parse(String content);

	public abstract boolean commitRates(String url, DatabaseService service, RestHelper rest);

	public abstract void logParsedValues();
}
