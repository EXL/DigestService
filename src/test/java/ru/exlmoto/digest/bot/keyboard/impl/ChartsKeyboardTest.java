package ru.exlmoto.digest.bot.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ChartsKeyboardTest {
	@Autowired
	private ChartsKeyboard keyboard;

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup());
		assertTrue(keyboard.getMarkup().inlineKeyboard().length > 20);
	}

	@Test
	public void testHandle() {

	}
}
