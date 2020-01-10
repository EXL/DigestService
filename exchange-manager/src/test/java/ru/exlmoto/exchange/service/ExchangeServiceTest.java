package ru.exlmoto.exchange.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.ExchangeConfigurationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ExchangeServiceTest extends ExchangeConfigurationTest {
	@Autowired
	private ExchangeService exchangeService;

	@Test
	public void testUpdateAllRates() {
		exchangeService.updateAllRates();
	}

	@Test
	public void testMarkdownReports() {
		assertThat(exchangeService.markdownBankRuReport()).isNotBlank();
		assertThat(exchangeService.markdownBankUaReport()).isNotBlank();
		assertThat(exchangeService.markdownBankByReport()).isNotBlank();
		assertThat(exchangeService.markdownBankKzReport()).isNotBlank();
		assertThat(exchangeService.markdownMetalRuReport()).isNotBlank();
	}

	@SpringBootApplication(scanBasePackageClasses = { ExchangeConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
