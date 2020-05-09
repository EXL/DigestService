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

package ru.exlmoto.digest.flat.parser.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlatN1ParserTest {
	@Autowired
	private FlatN1Parser parser;

	@Autowired
	private ResourceHelper helper;

	@Test
	void testGetAvailableFlats() {
		Answer<List<Flat>> res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1.json"));
		assertTrue(res.ok());
		assertEquals(res.answer().size(), 20);

		res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1-broken.json"));
		assertFalse(res.ok());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);

		res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1-empty.json"));
		assertFalse(res.ok());
		error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);
	}

	@Test
	void testParseLink() {
		assertNull(parser.parseLink(null));
		assertEquals(parser.parseLink(""), "");
		assertEquals(parser.parseLink("test"), "test");
		assertEquals(parser.parseLink("test"), "test");
		assertEquals(parser.parseLink("  a"), "  a");
		assertEquals(parser.parseLink("//test"), "https://test");
		assertEquals(parser.parseLink("  //test"), "https://test");
		assertEquals(parser.parseLink("http://example.com"), "http://example.com");
		assertEquals(parser.parseLink("https://example.com"), "https://example.com");
	}
}
