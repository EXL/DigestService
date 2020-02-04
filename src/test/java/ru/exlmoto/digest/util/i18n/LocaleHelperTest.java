package ru.exlmoto.digest.util.i18n;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LocaleHelperTest {
	@Autowired
	private LocaleHelper locale;

	@Test
	public void testLocalizationService() {
		String res = locale.i18n("exchange.error.report");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		res = locale.i18n("exchange.error.value") + " " + locale.i18n("exchange.change.up");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		assertEquals("???", locale.i18n("unknown.key"));
	}

	@Test
	public void testUsernameAndRandomLocalizations() {
		String stringWithUsername = locale.i18nU("bot.error.access", "Fake username");
		assertThat(stringWithUsername).isInstanceOf(String.class).isNotEmpty();
		System.out.println(stringWithUsername);

		String randomString = locale.i18nR("bot.event.user.new");
		assertThat(randomString).isInstanceOf(String.class).isNotEmpty();
		System.out.println(randomString);

		String randomStringWithUsername = locale.i18nRU("bot.event.user.new", "Fake username");
		assertThat(randomStringWithUsername).isInstanceOf(String.class).isNotEmpty();
		System.out.println(randomStringWithUsername);
	}
}
