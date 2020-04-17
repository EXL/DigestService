package ru.exlmoto.digest.service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DatabaseServiceTest {
	@Autowired
	private DatabaseService service;

	@Test
	public void testDatabaseService() {
		assertNotNull(service);
		assertTrue(service.checkGreeting(0L));
	}

	@Test
	public void testOnWrongPageArgument() {
		assertThrows(IllegalArgumentException.class, () -> service.getChatDigestsCommand(0, 10, 0L));
		assertThrows(IllegalArgumentException.class, () -> service.getAllDigests(0, 10));
	}
}
