package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.ExchangeService.ExchangeKey;

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
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_ru)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_ua)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_by)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_kz)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.metal_ru)).isNotBlank();
	}

	@Test
	public void testButtonLabels() {
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_ru)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_ua)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_by)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_kz)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.metal_ru)).isNotBlank();
	}
}
