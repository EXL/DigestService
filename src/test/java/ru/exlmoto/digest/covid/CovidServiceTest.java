package ru.exlmoto.digest.covid;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CovidServiceTest {
	@Autowired
	private CovidService service;

	@Test
	public void testJsonReport() {
		assertTrue(service.jsonRuReport().startsWith("{"));
		assertTrue(service.jsonUaReport().startsWith("{"));
	}

	@Test
	public void testTgHtmlReport() {
		assertTrue(service.tgHtmlRuReport().startsWith("<"));
		assertTrue(service.tgHtmlUaReport().startsWith("<"));
	}
}
