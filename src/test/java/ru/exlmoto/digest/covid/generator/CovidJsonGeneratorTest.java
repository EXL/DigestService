package ru.exlmoto.digest.covid.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class CovidJsonGeneratorTest {
	@Autowired
	private CovidJsonGenerator generator;

	@SpyBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Value("${covid.url}")
	private String covidUrl;

	@Test
	public void testGetJsonReport() {
		when(rest.getRestResponse(covidUrl + "/public/21.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/21.js", StandardCharsets.ISO_8859_1)));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22.js")));
		when(rest.getRestResponse(covidUrl + "/public/23.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/23.js")));

		String report = generator.getJsonReport(covidUrl);
		assertTrue(report.startsWith("{"));

		System.out.println(report);
	}

	@Test
	public void testGetJsonReportOnError() {
		when(rest.getRestResponse(covidUrl + "/public/21.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/21-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/22.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/22-broken.js")));
		when(rest.getRestResponse(covidUrl + "/public/23.js"))
			.thenReturn(Ok(helper.readFileToString("classpath:covid/23-broken.js")));

		String report = generator.getJsonReport(covidUrl);
		assertTrue(report.startsWith("{"));

		System.out.println(report);
	}
}
