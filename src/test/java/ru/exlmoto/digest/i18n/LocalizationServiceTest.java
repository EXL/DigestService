package ru.exlmoto.digest.i18n;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "general.lang=en")
class LocalizationServiceTest {
	@Autowired
	private LocalizationService l;

	@Test
	public void testLocalizationService() {
		assertEquals("wrote", l.i18n("motofan.wrote"));
		assertTrue(l.i18n("unknown.key").startsWith("No message found under code"));
	}
}
