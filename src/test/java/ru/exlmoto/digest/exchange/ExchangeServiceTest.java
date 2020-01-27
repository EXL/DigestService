package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.key.ExchangeKey;

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
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKey.metal_ru.name())).isNotBlank();
	}

	@Test
	public void testButtonLabels() {
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKey.metal_ru.name())).isNotBlank();
	}
}
