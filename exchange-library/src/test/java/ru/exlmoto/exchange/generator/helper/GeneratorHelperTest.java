package ru.exlmoto.exchange.generator.helper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import ru.exlmoto.exchange.ExchangeConfigurationTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorHelperTest extends ExchangeConfigurationTest {
	@Autowired
	private GeneratorHelper helper;

	@Test
	public void testInternationalization() {
		assertThat(helper.i18n("error.value")).isInstanceOf(String.class);
		assertEquals("<error>", helper.i18n("error.value"));
		assertThrows(NoSuchMessageException.class, () -> helper.i18n("unknown.value"));
	}

	@Test
	public void testGetDifference() {
		assertThat(helper.getDifference(null, null)).isNull();
		assertThat(helper.getDifference(new BigDecimal("1.0"), null)).isNull();
		assertThat(helper.getDifference(null, new BigDecimal("1.0"))).isNull();
		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.0"))).isNull();
		assertThat(helper.getDifference(new BigDecimal("0.0"), new BigDecimal("1.0"))).isNull();
		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("0.0"))).isNull();
		assertThat(helper.getDifference(new BigDecimal("0.0"), new BigDecimal("0.0"))).isNull();
		assertThat(helper.getDifference(new BigDecimal("1.0"), new BigDecimal("1.0"))).isNull();
		assertThat(
			helper.getDifference(
				new BigDecimal("4.0"),
				new BigDecimal("3.0")
			)
		).isEqualTo("1.0");
	}

	@Test
	public void testIsDifferencePositive() {
		assertTrue(helper.isDifferencePositive(new BigDecimal("1.0")));
		assertTrue(helper.isDifferencePositive(new BigDecimal("0.0000001")));
		assertFalse(helper.isDifferencePositive(new BigDecimal("0.0")));
		assertFalse(helper.isDifferencePositive(new BigDecimal("-0.0000001")));
		assertFalse(helper.isDifferencePositive(new BigDecimal("-1.0")));
	}

	@Test
	public void testNormalizeDifference() {
		assertEquals("-1.00", helper.normalizeDifference(new BigDecimal("-1")));
		assertEquals("-1.00", helper.normalizeDifference(new BigDecimal("-1.0")));
		assertEquals("-0.90", helper.normalizeDifference(new BigDecimal("-0.9")));
		assertEquals("0.00", helper.normalizeDifference(new BigDecimal("-0.0")));
		assertEquals("0.00", helper.normalizeDifference(new BigDecimal("0")));
		assertEquals("0.00", helper.normalizeDifference(new BigDecimal("0.0")));
		assertEquals("0.90", helper.normalizeDifference(new BigDecimal("0.9")));
		assertEquals("1.00", helper.normalizeDifference(new BigDecimal("1.0")));

		assertEquals("1234.12", helper.normalizeDifference(new BigDecimal("1234.1234")));
		assertEquals("1234.12", helper.normalizeDifference(new BigDecimal("1234.12340")));
		assertEquals("1234.12", helper.normalizeDifference(new BigDecimal("1234.12343")));
		assertEquals("1234.12", helper.normalizeDifference(new BigDecimal("1234.12345")));
		assertEquals("1234.13", helper.normalizeDifference(new BigDecimal("1234.12949")));
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
}
