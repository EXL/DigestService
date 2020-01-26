package ru.exlmoto.digest.bot.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChartsKeyboardTest {
	@Autowired
	private ChartsKeyboard keyboard;

	@Test
	public void testGetMarkup() {
		System.out.println(keyboard.getMarkup());
	}

	@Test
	public void testHandle() {
	}
}
