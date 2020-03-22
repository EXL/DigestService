package ru.exlmoto.digest.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseServiceTest {
	@Autowired
	private DatabaseService service;

	@Test
	public void testDatabaseService() {
		assertNotNull(service);
		assertTrue(service.checkGreeting(0L));
	}
}
