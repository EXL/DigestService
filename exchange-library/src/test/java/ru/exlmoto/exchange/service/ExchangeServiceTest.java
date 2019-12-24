package ru.exlmoto.exchange.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest("service.message=hello")
public class ExchangeServiceTest {
	@Autowired
	private ExchangeService exchangeService;

	@Test
	public void contextLoads() {
		assertThat(exchangeService.message()).isNotNull();
	}

	@SpringBootApplication
	static class TestConfiguration {

	}
}
