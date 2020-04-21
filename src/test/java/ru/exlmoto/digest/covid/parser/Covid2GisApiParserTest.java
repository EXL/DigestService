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

package ru.exlmoto.digest.covid.parser;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;

import ru.exlmoto.digest.covid.json.RegionFull;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.covid.helper.CovidConstants.CASES_RU_PATH;
import static ru.exlmoto.digest.covid.helper.CovidConstants.HISTORY_RU_PATH;
import static ru.exlmoto.digest.covid.helper.CovidConstants.CASES_UA_PATH;
import static ru.exlmoto.digest.covid.helper.CovidConstants.HISTORY_UA_PATH;
import static ru.exlmoto.digest.covid.helper.CovidConstants.CASES_RU_PATH_BROKEN;
import static ru.exlmoto.digest.covid.helper.CovidConstants.HISTORY_RU_PATH_BROKEN;
import static ru.exlmoto.digest.covid.helper.CovidConstants.CASES_UA_PATH_BROKEN;
import static ru.exlmoto.digest.covid.helper.CovidConstants.HISTORY_UA_PATH_BROKEN;
import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class Covid2GisApiParserTest {
	@Autowired
	private Covid2GisApiParser parser;

	@SpyBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Autowired
	private FilterHelper filter;

	@Value("${covid.url}")
	private String covidUrl;

	@Test
	public void testParse2GisApiDataRu() {
		when(rest.getRestResponse(filter.checkLink(covidUrl) + CASES_RU_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + CASES_RU_PATH)));
		when(rest.getRestResponse(filter.checkLink(covidUrl) + HISTORY_RU_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + HISTORY_RU_PATH)));

		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, CASES_RU_PATH, HISTORY_RU_PATH);
		assertTrue(res.ok());
	}

	@Test
	public void testParse2GisApiDataUa() {
		when(rest.getRestResponse(filter.checkLink(covidUrl) + CASES_UA_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + CASES_UA_PATH)));
		when(rest.getRestResponse(filter.checkLink(covidUrl) + HISTORY_UA_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + HISTORY_UA_PATH)));

		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, CASES_UA_PATH, HISTORY_UA_PATH);
		assertTrue(res.ok());
	}

	@Test
	public void testParse2GisApiDataRuBroken() {
		when(rest.getRestResponse(filter.checkLink(covidUrl) + CASES_RU_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + CASES_RU_PATH_BROKEN)));
		when(rest.getRestResponse(filter.checkLink(covidUrl) + HISTORY_RU_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + HISTORY_RU_PATH_BROKEN)));

		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, CASES_RU_PATH, HISTORY_RU_PATH);
		assertFalse(res.ok());
		System.out.println(res.error());
	}

	@Test
	public void testParse2GisApiDataUaBroken() {
		when(rest.getRestResponse(filter.checkLink(covidUrl) + CASES_UA_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + CASES_UA_PATH_BROKEN)));
		when(rest.getRestResponse(filter.checkLink(covidUrl) + HISTORY_UA_PATH))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/" + HISTORY_UA_PATH_BROKEN)));

		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, CASES_UA_PATH, HISTORY_UA_PATH);
		assertFalse(res.ok());
		System.out.println(res.error());
	}
}
