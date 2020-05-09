package ru.exlmoto.digest.flat.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class FlatTgHtmlGeneratorTest {
	@Autowired
	private FlatTgHtmlGenerator generator;

	@MockBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Test
	void testGetTgHtmlReportCian() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
	}

	@Test
	void testGetTgHtmlReportCianOnError() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-broken.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
	}

	@Test
	void testGetTgHtmlReportCianOnEmpty() {
		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-empty.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
	}

	@Test
	void testGetTgHtmlReportN1() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
	}

	@Test
	void testGetTgHtmlReportN1OnError() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-broken.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
	}

	@Test
	void testGetTgHtmlReportN1OnEmpty() {
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-empty.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
	}

	private void checkReport(String report) {
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}
}
