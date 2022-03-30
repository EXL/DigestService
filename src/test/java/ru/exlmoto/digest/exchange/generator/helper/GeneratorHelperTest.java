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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class GeneratorHelperTest {
	@Autowired
	private GeneratorHelper helper;

	@Test
	public void testGetDifference() {
		assertNull(helper.getDifference(null, null));
		assertNull(helper.getDifference(new BigDecimal("1.0"), null));
		assertNull(helper.getDifference(null, new BigDecimal("1.0")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.0")));
		assertNull(helper.getDifference(new BigDecimal("0.0"), new BigDecimal("1.0")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("0.0")));
		assertNull(helper.getDifference(new BigDecimal("0.0"), new BigDecimal("0.0")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.0")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.000001")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.00001")));

		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.005"))).isEqualTo("0.0050");
		assertThat(helper.getDifference(new BigDecimal("1.005"), new BigDecimal("1.0"))).isEqualTo("-0.0050");
		assertThat(helper.getDifference(new BigDecimal("4.0"), new BigDecimal("3.0"))).isEqualTo("-1.0000");

		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.00005"))).isEqualTo("0.0001");
		assertThat(helper.getDifference(new BigDecimal("1.00005"), new BigDecimal("1.0"))).isEqualTo("-0.0001");
		assertThat(helper.getDifference(new BigDecimal("4.0"), new BigDecimal("3.0"))).isEqualTo("-1.0000");
	}

	@Test
	public void testNormalizeValue() {
		assertEquals("-12.00000", helper.normalizeValue(new BigDecimal("-12"), 7));
		assertEquals("-1.000000", helper.normalizeValue(new BigDecimal("-1"), 7));
		assertEquals("-0.000100", helper.normalizeValue(new BigDecimal("-0.0001"), 7));
		assertEquals("0.000000", helper.normalizeValue(new BigDecimal("-0.0000"), 7));
		assertEquals("0.000000", helper.normalizeValue(new BigDecimal("0"), 7));
		assertEquals("0.000000", helper.normalizeValue(new BigDecimal("0.0"), 7));
		assertEquals("0.000000", helper.normalizeValue(new BigDecimal("0.0000"), 7));
		assertEquals("0.000100", helper.normalizeValue(new BigDecimal("0.0001"), 7));
		assertEquals("1.000000", helper.normalizeValue(new BigDecimal("1"), 7));
		assertEquals("1.000000", helper.normalizeValue(new BigDecimal("1.0"), 7));
		assertEquals("1.000100", helper.normalizeValue(new BigDecimal("1.0001"), 7));
		assertEquals("5.000000", helper.normalizeValue(new BigDecimal("5"), 7));
		assertEquals("12.00000", helper.normalizeValue(new BigDecimal("12"), 7));
		assertEquals("123.0000", helper.normalizeValue(new BigDecimal("123"), 7));
		assertEquals("1234.000", helper.normalizeValue(new BigDecimal("1234"), 7));
		assertEquals("12345.00", helper.normalizeValue(new BigDecimal("12345"), 7));
		assertEquals("12345.00", helper.normalizeValue(new BigDecimal("12345.0"), 7));
		assertEquals("12345.10", helper.normalizeValue(new BigDecimal("12345.1"), 7));
		assertEquals("12345.10", helper.normalizeValue(new BigDecimal("12345.10"), 7));
		assertEquals("12345.11", helper.normalizeValue(new BigDecimal("12345.11"), 7));
		assertEquals("12345.11", helper.normalizeValue(new BigDecimal("12345.110"), 7));
		assertEquals("12345.12", helper.normalizeValue(new BigDecimal("12345.119"), 7));
		assertEquals("12345.19", helper.normalizeValue(new BigDecimal("12345.19"), 7));
		assertEquals("12345.15", helper.normalizeValue(new BigDecimal("12345.149"), 7));
		assertEquals("12345.16", helper.normalizeValue(new BigDecimal("12345.161"), 7));
		assertEquals("123456.1", helper.normalizeValue(new BigDecimal("123456.1111"), 7));
		assertEquals("1234567.1", helper.normalizeValue(new BigDecimal("1234567.1111"), 7));
		assertEquals("12345678.1", helper.normalizeValue(new BigDecimal("12345678.1111"), 7));
		assertEquals("-1.000000",
			helper.normalizeValue(new BigDecimal("-1.00000000000000000000000000000000000000000000000000001"), 7));
	}

	@Test
	public void testAddTrailingSigns() {
		assertEquals("          ", helper.addTrailingSigns("", " ", 10));
		assertEquals("v         ", helper.addTrailingSigns("v", " ", 10));
		assertEquals("value     ", helper.addTrailingSigns("value", " ", 10));
		assertEquals("value_long", helper.addTrailingSigns("value_long", " ", 10));
		assertEquals("value_long1", helper.addTrailingSigns("value_long1", " ", 10));
		assertEquals("value_long_long", helper.addTrailingSigns("value_long_long", " ", 10));
	}

	@Test
	public void testAddLeadingSigns() {
		assertEquals("          ", helper.addLeadingSigns("", " ", 10));
		assertEquals("         v", helper.addLeadingSigns("v", " ", 10));
		assertEquals("     value", helper.addLeadingSigns("value", " ", 10));
		assertEquals("value_long", helper.addLeadingSigns("value_long", " ", 10));
		assertEquals("value_long1", helper.addLeadingSigns("value_long1", " ", 10));
		assertEquals("value_long_long", helper.addLeadingSigns("value_long_long", " ", 10));
	}

	@Test
	public void testIsDateNotEmpty() {
		assertFalse(helper.isDateNotEmpty(""));
		assertFalse(helper.isDateNotEmpty("null"));
		assertFalse(helper.isDateNotEmpty(null));

		assertTrue(helper.isDateNotEmpty("2020-01-08"));
		assertTrue(helper.isDateNotEmpty("08-JAN-2020"));
		assertTrue(helper.isDateNotEmpty("08/01/20"));
		assertTrue(helper.isDateNotEmpty("StringValue"));
	}

	@Test
	public void testGetValue() {
		assertEquals("0.0", helper.getValue(null));
		assertEquals("0.0", helper.getValue(""));
		assertEquals("0.0", helper.getValue("abc"));
		assertEquals("0", helper.getValue("0"));
		assertEquals("1", helper.getValue("1"));
		assertEquals("1.0", helper.getValue("1.0"));
	}
}
