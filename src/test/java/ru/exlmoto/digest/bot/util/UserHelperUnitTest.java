package ru.exlmoto.digest.bot.util;

import org.junit.jupiter.api.Test;

import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserHelperUnitTest {
	final UserHelper userHelper = new UserHelper();

	@Test
	public void testGetValidUsername() {
		User userFirst =
			new User(0, "First", false, null, null, "en");
		assertEquals("First", userHelper.getValidUsername(userFirst));

		User userSecond =
			new User(0, "First", false, "Last", null, "en");
		assertEquals("First Last", userHelper.getValidUsername(userSecond));

		User userThird =
			new User(0, "First", false, "Last", "User", "en");
		assertEquals("@User", userHelper.getValidUsername(userThird));

		User userFourth =
			new User(0, "First", false, null, "User", "en");
		assertEquals("@User", userHelper.getValidUsername(userFourth));
	}
}
