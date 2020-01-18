package ru.exlmoto.digest.bot.util;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BotHelperTest {
	@Autowired
	private BotHelper helper;

	@Test
	public void testNotNulls() {
		assertNotNull(helper.getSender());
		assertNotNull(helper.getLocale());
	}

	@Test
	public void testGetValidUsername() {
		User userFirst =
			new User(0, "First", false, null, null, "en");
		assertEquals("First", helper.getValidUsername(userFirst));

		User userSecond =
			new User(0, "First", false, "Last", null, "en");
		assertEquals("First Last", helper.getValidUsername(userSecond));

		User userThird =
			new User(0, "First", false, "Last", "User", "en");
		assertEquals("@User", helper.getValidUsername(userThird));

		User userFourth =
			new User(0, "First", false, null, "User", "en");
		assertEquals("@User", helper.getValidUsername(userFourth));
	}

	@Test
	public void testGetCurrentUnixTime() {
		String value = String.valueOf(helper.getCurrentUnixTime());
		assertEquals(10, value.length());
		System.out.println("Unix Time: " + value);
	}

	@Test
	public void testIsUserAdmin() {
		assertTrue(helper.isUserAdmin("exlmoto"));
		assertTrue(helper.isUserAdmin("ZorgeR"));
		assertFalse(helper.isUserAdmin("yakimka"));
	}
}
