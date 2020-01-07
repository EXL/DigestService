package ru.exlmoto.exchange.parser;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.parser.impl.BankUaParser;
import ru.exlmoto.exchange.parser.impl.BankByParser;
import ru.exlmoto.exchange.parser.impl.BankKzParser;
import ru.exlmoto.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.exchange.parser.impl.MetalRuMirrorParser;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class RateTest {
	@Autowired
	private ExchangeConfiguration configuration;

	@Autowired private BankRuParser bankRuParser;
	@Autowired private BankUaParser bankUaParser;
	@Autowired private BankUaMirrorParser bankUaMirror;
	@Autowired private BankByParser bankByParser;
	@Autowired private BankKzParser bankKzParser;
	@Autowired private MetalRuParser metalRuParser;
	@Autowired private MetalRuMirrorParser metalRuMirror;

	@Test
	public void testBankRu() {
		bankRuParser.process(configuration.getBankRu());
	}

	@Test
	public void testBankRuMirror() {
		bankRuParser.process(configuration.getBankRuMirror());
	}

	@Test
	public void testBankUa() {
		bankUaParser.process(configuration.getBankUa());
	}

	@Test
	public void testBankUaMirror() {
		bankUaMirror.process(configuration.getBankUaMirror());
	}

	@Test
	public void testBankBy() {
		bankByParser.process(configuration.getBankBy());
	}

	@Test
	public void testBankKz() {
		bankKzParser.process(configuration.getBankKz());
	}

	@Test
	public void testMetalRu() {
		metalRuParser.process(configuration.getMetalRu());
	}

	@Test
	public void testMetalRuMirror() {
		metalRuMirror.process(configuration.getMetalRuMirror());
	}
}
