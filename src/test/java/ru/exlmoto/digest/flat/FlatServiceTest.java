package ru.exlmoto.digest.flat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FlatServiceTest {
	@Autowired
	private FlatService service;

	@Test
	void testTgHtmlReportCian() {
		checkReport(service.tgHtmlReportCian("https://example.com", "https://example.com", 25));
	}

	@Test
	void testTgHtmlReportN1() {
		checkReport(service.tgHtmlReportN1("https://example.com", "https://example.com", 25));
	}

	private void checkReport(String report) {
		assertFalse(report.isEmpty());
		System.out.println(report);
	}
}
