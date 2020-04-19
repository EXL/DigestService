package ru.exlmoto.digest.site.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserFormUnitTest {
	private final UserForm form = new UserForm();

	@Test
	public void testCheckForm() {
		assertFalse(form.checkForm());
		form.setAvatar("avatar-url");
		assertFalse(form.checkForm());
		form.setId(0L);
		assertFalse(form.checkForm());
		form.setUsername("");
		assertFalse(form.checkForm());
		form.setUsername(" ");
		assertFalse(form.checkForm());
		form.setUsername("username");
		assertTrue(form.checkForm());
	}
}
