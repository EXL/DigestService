package ru.exlmoto.digest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class AnswerTest {
	@Test
	public void testAnswer() {
		Answer<String> answerFirst = new Answer<>("", "Ok!");
		assertTrue(answerFirst.ok());
		assertEquals("Ok!", answerFirst.answer());

		Answer<String> answerSecond = new Answer<>("Error text!", null);
		assertFalse(answerSecond.ok());
		assertNull(answerSecond.answer());
	}
}
