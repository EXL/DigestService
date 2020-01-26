package ru.exlmoto.digest.bot.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartKeyboardTest {
	@Autowired
	private ChartKeyboard keyboard;

	@Test
	public void testGetMarkup() {
		System.out.println(keyboard.getMarkup());
	}

	@Test
	public void testHandle() {
	}
}
