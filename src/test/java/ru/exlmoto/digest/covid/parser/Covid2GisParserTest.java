/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.covid.parser;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;

import ru.exlmoto.digest.covid.json.Region;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class Covid2GisParserTest {
	@Autowired
	private Covid2GisParser parser;

	@SpyBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Value("${covid.url}")
	private String covidUrl;

	@Test
	public void testParse2GisData() {
		when(rest.getRestResponse(covidUrl + "/public/21.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/21.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22.js")));
		when(rest.getRestResponse(covidUrl + "/public/23.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/23.js")));

		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		assertTrue(res.ok());
	}

	@Test
	public void testParse2GisDataBroken() {
		when(rest.getRestResponse(covidUrl + "/public/21.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/21-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/23.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/23-broken.js")));

		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		assertFalse(res.ok());
		System.out.println(res.error());
	}
}
