package ru.exlmoto.digest.i18n;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LocalizationHelperTest {
	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testLocalizationService() {
		String res = locale.i18n("exchange.error.report");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		res = locale.i18n("exchange.error.value") + " " + locale.i18n("exchange.change.up");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		assertTrue(locale.i18n("unknown.key").startsWith("No message found under code"));
	}
}
