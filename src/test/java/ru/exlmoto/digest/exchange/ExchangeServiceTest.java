package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeServiceTest {
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
}
