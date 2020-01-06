package ru.exlmoto.exchange.parser;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.parser.impl.BankRu;
import ru.exlmoto.exchange.parser.impl.BankUa;
import ru.exlmoto.exchange.parser.impl.BankBy;
import ru.exlmoto.exchange.parser.impl.BankKz;
import ru.exlmoto.exchange.parser.impl.BankUaMirror;
import ru.exlmoto.exchange.parser.impl.MetalRu;
import ru.exlmoto.exchange.parser.impl.MetalRuMirror;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class RateTest {
	@Autowired
	private ExchangeConfiguration configuration;

	@Autowired private BankRu bankRu;
	@Autowired private BankUa bankUa;
	@Autowired private BankUaMirror bankUaMirror;
	@Autowired private BankBy bankBy;
	@Autowired private BankKz bankKz;
	@Autowired private MetalRu metalRu;
	@Autowired private MetalRuMirror metalRuMirror;

	@Test
	public void testBankRu() {
		bankRu.process(configuration.getBankRu());
	}

	@Test
	public void testBankRuMirror() {
		bankRu.process(configuration.getBankRuMirror());
	}

	@Test
	public void testBankUa() {
		bankUa.process(configuration.getBankUa());
	}

	@Test
	public void testBankUaMirror() {
		bankUaMirror.process(configuration.getBankUaMirror());
	}

	@Test
	public void testBankBy() {
		bankBy.process(configuration.getBankBy());
	}

	@Test
	public void testBankKz() {
		bankKz.process(configuration.getBankKz());
	}

	@Test
	public void testMetalRu() {
		metalRu.process(configuration.getMetalRu());
	}

	@Test
	public void testMetalRuMirror() {
		metalRuMirror.process(configuration.getMetalRuMirror());
	}
}
