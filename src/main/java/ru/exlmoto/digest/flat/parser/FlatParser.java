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

package ru.exlmoto.digest.flat.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;

public abstract class FlatParser {
	private final Logger log = LoggerFactory.getLogger(FlatParser.class);

	protected final boolean onePhone = true;

	/*
	 * For convenience, prices with millions should use the number format that is used in the United States.
	 * Example: 2,300,300 rub.
	 */
	protected String adjustPrice(String price) {
		int parsed = -1;
		try {
			parsed = Integer.parseInt(price);
		} catch (NumberFormatException nfe) {
			log.error(String.format("Cannot parse price '%s' value to Integer.", price), nfe);
		}
		if (parsed != -1) {
			return NumberFormat.getInstance(Locale.US).format(parsed);
		}
		return price;
	}

	protected String applyPhonePatch(String phone) {
		if (StringUtils.hasText(phone) && phone.startsWith("+7")) {
			return "8" + phone.substring(2);
		}
		return phone;
	}

	public abstract Answer<List<Flat>> getAvailableFlats(String content);
}
