package ru.exlmoto.digest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class AnswerTest {
	@SuppressWarnings("ConstantConditions")
	@Test
	public void testAnswer() {
		Answer answerFirst = new Answer(true, "test");
		assertTrue(answerFirst.status());
		assertEquals("test", answerFirst.answer());

		Answer answerSecond = new Answer(false, null);
		assertFalse(answerSecond.status());
		assertNull(answerSecond.answer());
	}
}
