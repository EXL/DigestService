package ru.exlmoto.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.manager.impl.BankRuManager;
import ru.exlmoto.exchange.manager.impl.BankUaManager;
import ru.exlmoto.exchange.manager.impl.MetalRuManager;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.parser.impl.MetalRuParser;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@SpringBootApplication(scanBasePackages = "ru.exlmoto.exchange")
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class RateGeneralManagerTest {
	@Autowired
	private ExchangeConfiguration config;

	@Autowired private BankRuManager bankRuManager;
	@Autowired private BankUaManager bankUaManager;
	@Autowired private MetalRuManager metalRuManager;

	@Autowired private RestManager restManager;

	@Test
	public void testRateMirrors() {
		bankRuManager.commitRates(null, config.getBankRuMirror());
		bankUaManager.commitRates(null, config.getBankUaMirror());
		metalRuManager.commitRates(null, config.getMetalRuMirror());
	}

	@Test
	public void testIncorrectPages() {
		assertFalse(new BankRuParser().parse(restManager.getContent("https://exlmoto.ru")));
		assertFalse(new MetalRuParser().parse(restManager.getContent("https://exlmoto.ru")));
	}
}
