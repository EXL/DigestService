package ru.exlmoto.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.ExchangeConfigurationTest;
import ru.exlmoto.exchange.manager.impl.BankRuManager;
import ru.exlmoto.exchange.manager.impl.BankUaManager;
import ru.exlmoto.exchange.manager.impl.MetalRuManager;
import ru.exlmoto.rest.RestConfiguration;

public class RateGeneralManagerTest extends ExchangeConfigurationTest {
	@Autowired
	private ExchangeConfiguration configuration;

	@Autowired private BankRuManager bankRuManager;
	@Autowired private BankUaManager bankUaManager;
	@Autowired private MetalRuManager metalRuManager;

	@Test
	public void testRateMirrors() {
		bankRuManager.commitRates(null, configuration.getBankRuMirror());
		bankUaManager.commitRates(null, configuration.getBankUaMirror());
		metalRuManager.commitRates(null, configuration.getMetalRuMirror());
	}

	@SpringBootApplication(scanBasePackageClasses = { ExchangeConfiguration.class, RestConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
