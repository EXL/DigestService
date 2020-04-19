package ru.exlmoto.digest.site.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DigestFormUnitTest {
	private final DigestForm form = new DigestForm();

	@Test
	public void testCheckForm() {
		assertFalse(form.checkForm());
		form.setDigestId(0L);
		form.setMessageId(0L);
		assertFalse(form.checkForm());
		form.setChatId(0L);
		assertFalse(form.checkForm());
		form.setDate(0L);
		assertFalse(form.checkForm());
		form.setUserId(0L);
		assertFalse(form.checkForm());
		form.setDigest("");
		assertFalse(form.checkForm());
		form.setDigest(" ");
		assertFalse(form.checkForm());
		form.setDigest("Digest");
		assertTrue(form.checkForm());
	}
}
