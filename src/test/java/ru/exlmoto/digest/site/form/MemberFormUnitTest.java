package ru.exlmoto.digest.site.form;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.util.Role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberFormUnitTest {
	private final MemberForm form = new MemberForm();

	@Test
	public void testCheckForm() {
		assertFalse(form.checkForm());
		form.setId(0L);
		assertFalse(form.checkForm());
		form.setRole(Role.Administrator);
		assertFalse(form.checkForm());
		form.setUsername("");
		form.setPassword("");
		assertFalse(form.checkForm());
		form.setUsername(" ");
		form.setPassword(" ");
		assertFalse(form.checkForm());
		form.setUsername("user");
		form.setPassword("password");
		assertTrue(form.checkForm());
	}
}
