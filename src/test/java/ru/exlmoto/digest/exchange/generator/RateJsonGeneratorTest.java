package ru.exlmoto.digest.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.service.DatabaseService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@SpringBootTest
class RateJsonGeneratorTest {
	@Autowired
	private RateJsonGenerator generator;

	@SpyBean
	private DatabaseService service;

	@Test
	public void testGetJsonReport() {
		when(service.getBankRu()).thenReturn(Optional.ofNullable(null));
		String report = generator.getJsonReport();
		assertThat(report).isNotBlank();
		System.out.println(report);
	}

	@Test
	public void testGetJsonReportDatabaseError() {
		when(service.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		String report = generator.getJsonReport();
		assertThat(report).isNotBlank();
		System.out.println(report);
	}
}
