package ru.exlmoto.exchange.generator.helper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.ActiveProfiles;

import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class RepositoryHelperTest {
	@Autowired
	private RepositoryHelper repositoryHelper;

	@MockBean private BankRuRepository bankRepository;
	@MockBean private MetalRuRepository metalRepository;

	@Test
	public void testBankRepositoryNull() {
		when(bankRepository.getBankRu()).thenReturn(null);
		assertNull(repositoryHelper.getBankRu());
	}

	@Test
	public void testBankRepositoryException() {
		when(bankRepository.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertNull(repositoryHelper.getBankRu());
	}

	@Test
	public void testMetalRepositoryNull() {
		when(metalRepository.getMetalRu()).thenReturn(null);
		assertNull(repositoryHelper.getMetalRu());
	}

	@Test
	public void testMetalRepositoryException() {
		when(metalRepository.getMetalRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertNull(repositoryHelper.getMetalRu());
	}
}
