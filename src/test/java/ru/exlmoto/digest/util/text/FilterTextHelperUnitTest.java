package ru.exlmoto.digest.util.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterTextHelperUnitTest {
	private final FilterTextHelper filterTextHelper = new FilterTextHelper();

	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> filterTextHelper.removeHtmlTags(null));

		assertEquals("test text test", filterTextHelper.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", filterTextHelper.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", filterTextHelper.removeHtmlTags("test <asdasd> text test</..."));
		assertEquals("test text test", filterTextHelper.removeHtmlTags("test <blockquote> text test</block"));
	}

	@Test
	public void testRemoveBbTags() {
		assertThrows(NullPointerException.class, () -> filterTextHelper.removeBbCodes(null));

		assertEquals("test text test", filterTextHelper.removeBbCodes("test [b]text[/b] test"));
		assertEquals("test text test bl", filterTextHelper.removeBbCodes("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", filterTextHelper.removeBbCodes("test [b]text[/b] test [\\bl"));
	}

	@Test
	public void testCheckLink() {
		assertEquals("https://t.me/", filterTextHelper.checkLink("https://t.me"));
		assertEquals("https://t.me/", filterTextHelper.checkLink("https://t.me/"));
	}

	@Test
	public void testGetDateFromTimeStamp() {
		System.out.println(filterTextHelper.getDateFromTimeStamp("dd.MM.yyyy HH:mm", 1580520624L));
		System.out.println(filterTextHelper.getDateFromTimeStamp("dd.MMM.yyyy HH:mm", 1580520624L));
		System.out.println(filterTextHelper.getDateFromTimeStamp("dd-MM-yyyy HH:mm", 1580585762L));
		System.out.println(filterTextHelper.getDateFromTimeStamp("dd-MMM-yyyy HH:mm", 1580585762L));
	}
}
