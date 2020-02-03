package ru.exlmoto.digest.bot.ability.keyboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyboardUnitTest {
	@Test
	public void testChopKeyboardNameLeft() {
		assertEquals("", Keyboard.chopKeyboardNameLeft(Keyboard.subscribe.withName()));
		assertEquals("digest", Keyboard.chopKeyboardNameLeft(Keyboard.subscribe.withName() + "digest"));
		assertEquals("digest_", Keyboard.chopKeyboardNameLeft(Keyboard.subscribe.withName() + "digest_"));
		assertEquals("digest_subscribe",
			Keyboard.chopKeyboardNameLeft(Keyboard.subscribe.withName() + "digest_subscribe"));
	}

	@Test
	public void testChopKeyboardNameRight() {
		assertEquals("chart_", Keyboard.chopKeyboardNameRight("chart_rub"));
		assertEquals("chart_", Keyboard.chopKeyboardNameRight("chart_rub_eur"));
		assertEquals("chart", Keyboard.chopKeyboardNameRight("chart"));
		assertEquals("chart_", Keyboard.chopKeyboardNameRight("chart___"));
		assertEquals("chart_", Keyboard.chopKeyboardNameRight("chart__"));
		assertEquals("chart_", Keyboard.chopKeyboardNameRight("chart_"));
		assertEquals("_", Keyboard.chopKeyboardNameRight("_"));
		assertEquals("_", Keyboard.chopKeyboardNameRight("_a"));
		assertEquals("_", Keyboard.chopKeyboardNameRight("_ab"));
	}

	@Test
	public void testChopKeyboardNameLast() {
		assertEquals("rub", Keyboard.chopKeyboardNameLast("chart_rub"));
		assertEquals("eur", Keyboard.chopKeyboardNameLast("chart_rub_eur"));
		assertEquals("chart", Keyboard.chopKeyboardNameLast("chart"));
		assertEquals("", Keyboard.chopKeyboardNameLast("chart___"));
		assertEquals("", Keyboard.chopKeyboardNameLast("chart__"));
		assertEquals("", Keyboard.chopKeyboardNameLast("chart_"));
		assertEquals("", Keyboard.chopKeyboardNameLast("_"));
		assertEquals("a", Keyboard.chopKeyboardNameLast("_a"));
		assertEquals("ab", Keyboard.chopKeyboardNameLast("_ab"));
		assertEquals("page", Keyboard.chopKeyboardNameLast("digest_page"));
		assertEquals("1", Keyboard.chopKeyboardNameLast("digest_page_1"));
		assertEquals("11312", Keyboard.chopKeyboardNameLast("show_page_11312"));
	}
}
