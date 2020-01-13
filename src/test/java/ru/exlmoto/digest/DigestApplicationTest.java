package ru.exlmoto.digest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DigestApplicationTest {
	@Autowired
	private DigestApplication application;

	@Test
	void contextLoads() {
		assertNotNull(application);
	}
}
