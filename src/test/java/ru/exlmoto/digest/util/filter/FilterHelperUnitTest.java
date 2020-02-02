package ru.exlmoto.digest.util.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterHelperUnitTest {
	private final FilterHelper filterHelper = new FilterHelper();

	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> filterHelper.removeHtmlTags(null));

		assertEquals("test text test", filterHelper.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", filterHelper.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", filterHelper.removeHtmlTags("test <asdasd> text test</..."));
		assertEquals("test text test", filterHelper.removeHtmlTags("test <blockquote> text test</block"));
	}

	@Test
	public void testRemoveBbTags() {
		assertThrows(NullPointerException.class, () -> filterHelper.removeBbCodes(null));

		assertEquals("test text test", filterHelper.removeBbCodes("test [b]text[/b] test"));
		assertEquals("test text test bl", filterHelper.removeBbCodes("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", filterHelper.removeBbCodes("test [b]text[/b] test [\\bl"));
	}

	@Test
	public void testCheckLink() {
		assertEquals("https://t.me/", filterHelper.checkLink("https://t.me"));
		assertEquals("https://t.me/", filterHelper.checkLink("https://t.me/"));
	}

	@Test
	public void testGetDateFromTimeStamp() {
		System.out.println(filterHelper.getDateFromTimeStamp("dd.MM.yyyy HH:mm", 1580520624L));
		System.out.println(filterHelper.getDateFromTimeStamp("dd.MMM.yyyy HH:mm", 1580520624L));
		System.out.println(filterHelper.getDateFromTimeStamp("dd-MM-yyyy HH:mm", 1580585762L));
		System.out.println(filterHelper.getDateFromTimeStamp("dd-MMM-yyyy HH:mm", 1580585762L));
	}
}
