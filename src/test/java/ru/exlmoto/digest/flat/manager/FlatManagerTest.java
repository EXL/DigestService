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

package ru.exlmoto.digest.flat.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class FlatManagerTest {
	@Autowired
	private FlatManager manager;

	@MockBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Autowired
	private FlatCianParser parser;

	@Test
	public void testGetCianFlatList() {
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian.xlsx")));

		checkGoodAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	public void testGetCianFlatListOnError() {
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-broken.xlsx")));

		checkBadAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	public void testGetCianFlatListOnEmpty() {
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-empty.xlsx")));

		checkBadAnswer(manager.getCianFlatList("https://example.com"));
	}

	@Test
	public void testGetN1FlatList() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1.json")));

		checkGoodAnswer(manager.getN1FlatList("https://example.com"));
	}

	@Test
	public void testGetN1FlatListOnError() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-broken.json")));

		checkBadAnswer(manager.getN1FlatList("https://example.com"));
	}

	@Test
	public void testGetN1FlatListOnEmpty() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-empty.json")));

		checkBadAnswer(manager.getN1FlatList("https://example.com"));
	}

	private void checkGoodAnswer(Answer<List<Flat>> answer) {
		assertTrue(answer.ok());
		assertFalse(answer.answer().isEmpty());
	}

	private void checkBadAnswer(Answer<List<Flat>> answer) {
		assertFalse(answer.ok());
		String error = answer.error();
		assertTrue(StringUtils.hasText(error));
		System.out.println(error);
	}
}
