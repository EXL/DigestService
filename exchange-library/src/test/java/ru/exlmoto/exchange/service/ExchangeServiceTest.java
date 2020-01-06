package ru.exlmoto.exchange.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.parser.impl.BankRu;
import ru.exlmoto.exchange.parser.impl.BankUa;
import ru.exlmoto.exchange.parser.impl.BankUaMirror;
import ru.exlmoto.exchange.parser.impl.BankBy;
import ru.exlmoto.exchange.parser.impl.BankKz;
import ru.exlmoto.exchange.parser.impl.MetalRu;
import ru.exlmoto.exchange.parser.impl.MetalRuMirror;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
public class ExchangeServiceTest {
	@Autowired
	private ExchangeService exchangeService;

	@Test
	public void testUpdateExchange() {
		exchangeService.updateAllRates();
	}

	@Test
	public void testExchangeReports() {
		assertThat(exchangeService.markdownBankRuReport()).isNotBlank();
		assertThat(exchangeService.markdownBankUaReport()).isNotBlank();
		assertThat(exchangeService.markdownBankByReport()).isNotBlank();
		assertThat(exchangeService.markdownBankKzReport()).isNotBlank();
		assertThat(exchangeService.markdownMetalRuReport()).isNotBlank();
	}
}
