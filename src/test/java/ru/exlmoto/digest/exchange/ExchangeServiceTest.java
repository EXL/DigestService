package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.util.ExchangeKeys;

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
		assertThat(exchangeService.markdownReport(ExchangeKeys.BANK_RU)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKeys.BANK_UA)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKeys.BANK_BY)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKeys.BANK_KZ)).isNotBlank();
		assertThat(exchangeService.markdownReport(ExchangeKeys.METAL_RU)).isNotBlank();
	}

	@Test
	public void testButtonLabels() {
		assertThat(exchangeService.buttonLabel(ExchangeKeys.BANK_RU)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKeys.BANK_UA)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKeys.BANK_BY)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKeys.BANK_KZ)).isNotBlank();
		assertThat(exchangeService.buttonLabel(ExchangeKeys.METAL_RU)).isNotBlank();
	}
}
