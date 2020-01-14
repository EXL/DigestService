package ru.exlmoto.digest.exchange.generator.helper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import ru.exlmoto.digest.repository.BankByRepository;
import ru.exlmoto.digest.repository.BankRuRepository;
import ru.exlmoto.digest.repository.BankUaRepository;
import ru.exlmoto.digest.repository.BankKzRepository;
import ru.exlmoto.digest.repository.MetalRuRepository;

import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.Mockito.when;

@SpringJUnitConfig
@ContextConfiguration(classes = { RepositoryHelper.class })
class RepositoryHelperTest {
	@Autowired
	private RepositoryHelper repositoryHelper;

	@MockBean private BankRuRepository bankRuRepository;
	@MockBean private BankUaRepository bankUaRepository;
	@MockBean private BankByRepository bankByRepository;
	@MockBean private BankKzRepository bankKzRepository;
	@MockBean private MetalRuRepository metalRuRepository;

	@Test
	public void testBankRepositoryNull() {
		when(bankRuRepository.getBankRu()).thenReturn(null);
		assertNull(repositoryHelper.getBankRu());
	}

	@Test
	public void testBankRepositoryException() {
		when(bankRuRepository.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertNull(repositoryHelper.getBankRu());
	}

	@Test
	public void testMetalRepositoryNull() {
		when(metalRuRepository.getMetalRu()).thenReturn(null);
		assertNull(repositoryHelper.getMetalRu());
	}

	@Test
	public void testMetalRepositoryException() {
		when(metalRuRepository.getMetalRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertNull(repositoryHelper.getMetalRu());
	}
}
