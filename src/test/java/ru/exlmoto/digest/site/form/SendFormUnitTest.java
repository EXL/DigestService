package ru.exlmoto.digest.site.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendFormUnitTest {
	private final SendForm form = new SendForm();

	@Test
	public void testCheckSend() {
		assertFalse(form.checkSend());
		form.setSendChatId(0L);
		assertFalse(form.checkSend());
		form.setSendChatArg("");
		assertFalse(form.checkSend());
		form.setSendChatArg(" ");
		assertFalse(form.checkSend());
		form.setSendChatArg("value");
		assertTrue(form.checkSend());
	}

	@Test
	public void testCheckSticker() {
		assertFalse(form.checkSticker());
		form.setStickerChatId(0L);
		assertFalse(form.checkSticker());
		form.setStickerChatArg("");
		assertFalse(form.checkSticker());
		form.setStickerChatArg(" ");
		assertFalse(form.checkSticker());
		form.setStickerChatArg("value");
		assertTrue(form.checkSticker());
	}

	@Test
	public void testCheckImage() {
		assertFalse(form.checkImage());
		form.setImageChatId(0L);
		assertFalse(form.checkImage());
		form.setImageChatArg("");
		assertFalse(form.checkImage());
		form.setImageChatArg(" ");
		assertFalse(form.checkImage());
		form.setImageChatArg("value");
		assertTrue(form.checkImage());
	}
}
