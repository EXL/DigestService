package ru.exlmoto.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.ExchangeConfigurationTest;
import ru.exlmoto.exchange.manager.impl.BankUaManager;
import ru.exlmoto.exchange.repository.BankUaRepository;

import static org.mockito.Mockito.when;

public class RateManagerTest extends ExchangeConfigurationTest {
	@Autowired
	private ExchangeConfiguration configuration;

	@MockBean
	private BankUaRepository bankUaRepository;

	@Autowired
	private BankUaManager bankUaManager;

	@Test
	public void testRateException() {
		when(bankUaRepository.getBankUa()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		bankUaManager.commitRates(configuration.getBankUa());
	}

	@SpringBootApplication(scanBasePackageClasses = { ExchangeConfiguration.class })
	public static class ExchangeConfigurationCommon {

	}
}
