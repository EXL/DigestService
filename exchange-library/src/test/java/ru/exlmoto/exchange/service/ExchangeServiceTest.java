package ru.exlmoto.exchange.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExchangeServiceTest {
	@Autowired
	private ExchangeService exchangeService;

	@Test
	public void contextLoads() {
		exchangeService.testAllSources();
	}

	@SpringBootApplication
	static class TestConfiguration {

	}
}
