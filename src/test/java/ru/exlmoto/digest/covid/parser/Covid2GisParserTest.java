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
		when(rest.getRestResponse(covidUrl + "/public/13.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/13.js")));
		when(rest.getRestResponse(covidUrl + "/public/14.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/14.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22.js")));

		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		assertTrue(res.ok());
	}

	@Test
	public void testParse2GisDataBroken() {
		when(rest.getRestResponse(covidUrl + "/public/13.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/13-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/14.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/14-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22-broken.js")));

		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		assertFalse(res.ok());
		System.out.println(res.error());
	}
}
