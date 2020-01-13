package ru.exlmoto.digest.i18n;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "general.lang=en")
class LocalizationServiceTest {
	@Autowired
	private LocalizationService locale;

	@Test
	public void testLocalizationService() {
		assertEquals("wrote", locale.i18n("motofan.wrote"));
		assertTrue(locale.i18n("unknown.key").startsWith("No message found under code"));
	}
}
