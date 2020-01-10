package ru.exlmoto.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.ExchangeConfigurationTest;
import ru.exlmoto.exchange.manager.impl.BankRuManager;
import ru.exlmoto.exchange.manager.impl.BankUaManager;
import ru.exlmoto.exchange.manager.impl.MetalRuManager;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.parser.impl.MetalRuParser;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class RateGeneralManagerTest extends ExchangeConfigurationTest {
	@Autowired
	private ExchangeConfiguration configuration;

	@Autowired private BankRuManager bankRuManager;
	@Autowired private BankUaManager bankUaManager;
	@Autowired private MetalRuManager metalRuManager;

	@Autowired private RestManager restManager;

	@Test
	public void testRateMirrors() {
		bankRuManager.commitRates(null, configuration.getBankRuMirror());
		bankUaManager.commitRates(null, configuration.getBankUaMirror());
		metalRuManager.commitRates(null, configuration.getMetalRuMirror());
	}

	@Test
	public void testIncorrectPages() {
		assertFalse(new BankRuParser().parse(restManager.getContent("https://exlmoto.ru")));
		assertFalse(new MetalRuParser().parse(restManager.getContent("https://exlmoto.ru")));
	}
}
