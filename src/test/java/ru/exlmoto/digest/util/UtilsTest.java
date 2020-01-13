package ru.exlmoto.digest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTest {
	private final Utils utils = new Utils();

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> utils.removeHtmlTags(null));

		assertEquals("test text test", utils.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", utils.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", utils.removeHtmlTags("test <asdasd> text test</..."));
		assertEquals("test text test", utils.removeHtmlTags("test <blockquote> text test</block"));
	}
}
