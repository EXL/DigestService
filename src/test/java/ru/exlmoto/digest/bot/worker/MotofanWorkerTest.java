package ru.exlmoto.digest.bot.worker;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MotofanWorkerTest {
	@Autowired
	private MotofanWorker worker;

	@Test
	public void contextLoads() {
		// TODO: ???
		assertNotNull(worker);
	}
}
