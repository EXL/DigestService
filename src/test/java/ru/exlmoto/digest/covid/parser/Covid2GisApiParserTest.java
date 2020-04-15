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
