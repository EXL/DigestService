package ru.exlmoto.digest.covid.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class CovidTgHtmlGeneratorTest {
	@Autowired
	private CovidTgHtmlGenerator generator;

	@SpyBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Value("${covid.url}")
	private String covidUrl;

	@Test
	public void testGetTgHtmlReport() {
		when(rest.getRestResponse(covidUrl + "/public/13.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/13-next.js", StandardCharsets.ISO_8859_1)));
		when(rest.getRestResponse(covidUrl + "/public/14.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/14-next.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22-next.js")));

		String report = generator.getTgHtmlReport(covidUrl);
		assertTrue(report.startsWith("<"));

		System.out.println(report);
	}

	@Test
	public void testGetTgHtmlReportOnError() {
		when(rest.getRestResponse(covidUrl + "/public/13.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/13-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/14.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/14-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22-broken.js")));

		String report = generator.getTgHtmlReport(covidUrl);
		assertNull(report);
	}
}
