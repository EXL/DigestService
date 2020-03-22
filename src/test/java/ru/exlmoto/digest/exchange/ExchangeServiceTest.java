package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.key.ExchangeKey;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeServiceTest {
	@Autowired
	private ExchangeService service;

	@Test
	public void testUpdateAllRates() {
		service.updateAllRates();
	}

	@Test
	public void testMarkdownReports() {
		assertThat(service.markdownReport(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.metal_ru.name())).isNotBlank();
	}

	@Test
	public void testJsonReport() {
		assertThat(service.jsonReport()).isNotBlank();
	}

	@Test
	public void testButtonLabels() {
		assertThat(service.buttonLabel(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.metal_ru.name())).isNotBlank();
	}
}
