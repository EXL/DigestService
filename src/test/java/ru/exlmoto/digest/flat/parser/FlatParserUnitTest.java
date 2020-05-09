package ru.exlmoto.digest.flat.parser;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.ArgumentMatchers.anyString;

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
		assertNull(parser.getAvailableFlats(anyString()));
	}
}
