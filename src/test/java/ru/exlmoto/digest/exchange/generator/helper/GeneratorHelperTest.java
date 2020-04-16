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
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.0001")));
		assertNull(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.001")));

		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.005"))).isEqualTo("-0.01");
		assertThat(helper.getDifference(new BigDecimal("1.005"), new BigDecimal("1.0"))).isEqualTo("0.01");
		assertThat(helper.getDifference(new BigDecimal("4.0"), new BigDecimal("3.0"))).isEqualTo("1.00");
	}
	
	@Test
	public void testNormalizeValue() {
		assertEquals("-12.0000", helper.normalizeValue(new BigDecimal("-12")));
		assertEquals("-1.00000", helper.normalizeValue(new BigDecimal("-1")));
		assertEquals("-0.00010", helper.normalizeValue(new BigDecimal("-0.0001")));
		assertEquals("0.00000", helper.normalizeValue(new BigDecimal("-0.0000")));
		assertEquals("0.00000", helper.normalizeValue(new BigDecimal("0")));
		assertEquals("0.00000", helper.normalizeValue(new BigDecimal("0.0")));
		assertEquals("0.00000", helper.normalizeValue(new BigDecimal("0.0000")));
		assertEquals("0.00010", helper.normalizeValue(new BigDecimal("0.0001")));
		assertEquals("1.00000", helper.normalizeValue(new BigDecimal("1")));
		assertEquals("1.00000", helper.normalizeValue(new BigDecimal("1.0")));
		assertEquals("1.00010", helper.normalizeValue(new BigDecimal("1.0001")));
		assertEquals("5.00000", helper.normalizeValue(new BigDecimal("5")));
		assertEquals("12.0000", helper.normalizeValue(new BigDecimal("12")));
		assertEquals("123.000", helper.normalizeValue(new BigDecimal("123")));
		assertEquals("1234.00", helper.normalizeValue(new BigDecimal("1234")));
		assertEquals("12345.0", helper.normalizeValue(new BigDecimal("12345")));
		assertEquals("12345.0", helper.normalizeValue(new BigDecimal("12345.0")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.1")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.10")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.11")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.110")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.119")));
		assertEquals("12345.2", helper.normalizeValue(new BigDecimal("12345.19")));
		assertEquals("12345.1", helper.normalizeValue(new BigDecimal("12345.149")));
		assertEquals("12345.2", helper.normalizeValue(new BigDecimal("12345.161")));
		assertEquals("123456.1", helper.normalizeValue(new BigDecimal("123456.1111")));
		assertEquals("1234567.1", helper.normalizeValue(new BigDecimal("1234567.1111")));
		assertEquals("12345678.1", helper.normalizeValue(new BigDecimal("12345678.1111")));
		assertEquals("-1.00000",
			helper.normalizeValue(new BigDecimal("-1.00000000000000000000000000000000000000000000000000001")));
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
