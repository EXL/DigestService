package ru.exlmoto.digest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

class AnswerUnitTest {
	@Test
	public void testAnswer() {
		Answer<String> answerFirst = Ok("Ok!");
		assertTrue(answerFirst.ok());
		assertEquals("Ok!", answerFirst.answer());

		Answer<String> answerSecond = Error("Error text!");
		assertFalse(answerSecond.ok());
		assertNull(answerSecond.answer());
	}
}
