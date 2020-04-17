package ru.exlmoto.digest.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.service.DatabaseService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@SpringBootTest
class ExchangeManagerTest {
	@Autowired
	private ExchangeManager manager;

	@Autowired
	private ExchangeConfiguration config;

	@MockBean
	private DatabaseService service;

	@Test
	public void testRateException() {
		when(service.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		manager.commitBankRu(config.getBankRu(), config.getBankRuMirror());

		when(service.getBankUa()).thenReturn(null);
		assertNull(service.getBankUa());

		when(service.getMetalRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertThrows(InvalidDataAccessResourceUsageException.class, () -> service.getMetalRu());
	}

	@Test
	public void testRateMirrors() {
		manager.commitBankRu(null, config.getBankRuMirror());
		manager.commitBankUa(null, config.getBankUaMirror());
		manager.commitMetalRu(null, config.getMetalRuMirror());
	}
}
