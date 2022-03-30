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

package ru.exlmoto.digest.exchange.generator.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GeneratorHelper {
	private final Logger log = LoggerFactory.getLogger(GeneratorHelper.class);

	public String getDifference(BigDecimal prev, BigDecimal current) {
		if (prev == null || current == null) {
			return null;
		}
		if ((prev.signum() == 0) || (current.signum() == 0)) {
			return null;
		}
		if (prev.compareTo(current) == 0) {
			return null;
		}
		BigDecimal difference = new BigDecimal(String.format("%.4f", current.subtract(prev)));
		if (difference.signum() == 0) {
			return null;
		}
		return difference.toString();
	}

	public String getValue(String diff) {
		if (diff != null && !diff.isEmpty()) {
			try {
				new BigDecimal(diff);
				return diff;
			} catch (NumberFormatException nfe) {
				log.error(String.format("Cannot parse number value from '%s' string.", diff), nfe);
			}
		}
		return "0.0";
	}

	public String normalizeValue(BigDecimal value, int maxNumberSize) {
		int integers = value.precision() - value.scale();
		if (integers <= 0) {
			integers = 1;
		}
		if (integers < maxNumberSize) {
			return String.format("%." + (maxNumberSize - integers) + "f", value);
		}
		return String.format("%.1f", value);
	}

	public String addTrailingSigns(String value, String sign, int limit) {
		StringBuilder builder = new StringBuilder(value);
		int start = value.length();
		for (int i = start; i < limit; i++) {
			builder.append(sign);
		}
		return builder.toString();
	}

	public String addLeadingSigns(String value, String sign, int limit) {
		StringBuilder builder = new StringBuilder();
		int start = value.length();
		for (int i = start; i < limit; i++) {
			builder.append(sign);
		}
		return builder + value;
	}

	public boolean isDateNotEmpty(String date) {
		return date != null && !date.isEmpty() && !date.equals("null");
	}
}
