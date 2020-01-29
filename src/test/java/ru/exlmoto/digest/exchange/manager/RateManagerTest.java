package ru.exlmoto.digest.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.exchange.manager.impl.BankUaManager;

import static org.mockito.Mockito.when;

@SpringBootTest
class RateManagerTest {
	@Value("${exchange.bank-ua}")
	private String bankUa;

	@MockBean
	private BankUaRepository bankUaRepository;

	@Autowired
	private BankUaManager bankUaManager;

	@Test
	public void testRateException() {
		when(bankUaRepository.getBankUa()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		bankUaManager.commitRates(bankUa);
	}
}
