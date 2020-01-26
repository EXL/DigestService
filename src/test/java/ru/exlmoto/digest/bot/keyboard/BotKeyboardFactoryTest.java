package ru.exlmoto.digest.bot.keyboard;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class BotKeyboardFactoryTest {
	@Autowired
	private BotKeyboardFactory keyboardFactory;

	@Test
	public void testGetKeyboard() {
		assertTrue(keyboardFactory.getKeyboard(BotKeyboard.CHART).isPresent());
		assertFalse(keyboardFactory.getKeyboard("unknown-keyboard").isPresent());
	}
}
