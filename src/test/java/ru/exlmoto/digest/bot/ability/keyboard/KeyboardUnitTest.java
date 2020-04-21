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
