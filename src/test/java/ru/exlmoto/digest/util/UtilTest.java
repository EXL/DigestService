package ru.exlmoto.digest.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilTest {
	private final Util util = new Util();

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> util.removeHtmlTags(null));

		assertEquals("test text test", util.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", util.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", util.removeHtmlTags("test <asdasd> text test</..."));
		assertEquals("test text test", util.removeHtmlTags("test <blockquote> text test</block"));
	}

	@SuppressWarnings("ConstantConditions")
	@Test
	public void testRemoveBbTags() {
		assertThrows(NullPointerException.class, () -> util.removeBbCodes(null));

		assertEquals("test text test", util.removeBbCodes("test [b]text[/b] test"));
		assertEquals("test text test bl", util.removeBbCodes("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", util.removeBbCodes("test [b]text[/b] test [\\bl"));
	}
}
