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

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FlatParserUnitTest {
	private final FlatParser parser = new FlatParser() {
		@Override
		public Answer<List<Flat>> getAvailableFlats(String content) {
			return null;
		}
	};

	@Test
	void testAdjustPrice() {
		assertNull(parser.adjustPrice(null));
		assertEquals(parser.adjustPrice(""), "");
		assertEquals(parser.adjustPrice("string"), "string");

		assertEquals(parser.adjustPrice("1.0"), "1.0");
		assertEquals(parser.adjustPrice("100000000.04"), "100000000.04");

		assertEquals(parser.adjustPrice("0"), "0");
		assertEquals(parser.adjustPrice("1"), "1");
		assertEquals(parser.adjustPrice("10"), "10");
		assertEquals(parser.adjustPrice("100"), "100");
		assertEquals(parser.adjustPrice("1000"), "1,000");
		assertEquals(parser.adjustPrice("10000"), "10,000");
		assertEquals(parser.adjustPrice("100000"), "100,000");
		assertEquals(parser.adjustPrice("1000000"), "1,000,000");
		assertEquals(parser.adjustPrice("10000000"), "10,000,000");
		assertEquals(parser.adjustPrice("100000000"), "100,000,000");

		assertEquals(parser.adjustPrice("-0"), "0");
		assertEquals(parser.adjustPrice("-1"), "-1");
		assertEquals(parser.adjustPrice("-10"), "-10");
		assertEquals(parser.adjustPrice("-100"), "-100");
		assertEquals(parser.adjustPrice("-1000"), "-1,000");
		assertEquals(parser.adjustPrice("-10000"), "-10,000");
		assertEquals(parser.adjustPrice("-100000"), "-100,000");
		assertEquals(parser.adjustPrice("-1000000"), "-1,000,000");
		assertEquals(parser.adjustPrice("-10000000"), "-10,000,000");
		assertEquals(parser.adjustPrice("-100000000"), "-100,000,000");
	}

	@Test
	void testApplyPhonePatch() {
		assertEquals(parser.applyPhonePatch(""), "");
		assertEquals(parser.applyPhonePatch("+113434234"), "+113434234");
		assertEquals(parser.applyPhonePatch("713434234"), "713434234");
		assertEquals(parser.applyPhonePatch("+713434234"), "813434234");
	}

	@Test
	void testGetAvailableFlats() {
		assertNull(parser.getAvailableFlats("https://example.com"));
	}
}
