package ru.exlmoto.digest.util.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterHelperUnitTest {
	private final FilterHelper filter = new FilterHelper();

	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> filter.removeHtmlTags(null));

		assertEquals("test text test", filter.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", filter.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", filter.removeHtmlTags("test <asdasd> text test</..."));
		assertEquals("test text test", filter.removeHtmlTags("test <blockquote> text test</block"));
	}

	@Test
	public void testRemoveBbTags() {
		assertThrows(NullPointerException.class, () -> filter.removeBbCodes(null));

		assertEquals("test text test", filter.removeBbCodes("test [b]text[/b] test"));
		assertEquals("test text test bl", filter.removeBbCodes("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", filter.removeBbCodes("test [b]text[/b] test [\\bl"));
	}

	@Test
	public void testCheckLink() {
		assertEquals("https://t.me/", filter.checkLink("https://t.me"));
		assertEquals("https://t.me/", filter.checkLink("https://t.me/"));
	}

	@Test
	public void testGetDateFromTimeStamp() {
		System.out.println(filter.getDateFromTimeStamp("dd.MM.yyyy HH:mm", 1580520624L));
		System.out.println(filter.getDateFromTimeStamp("dd.MMM.yyyy HH:mm", 1580520624L));
		System.out.println(filter.getDateFromTimeStamp("dd-MM-yyyy HH:mm", 1580585762L));
		System.out.println(filter.getDateFromTimeStamp("dd-MMM-yyyy HH:mm", 1580585762L));
	}

	@Test
	public void testStrip() {
		assertEquals(
			"exlmoto test@test ada@adasd.ru azxczxz asd zc@ adsa@ asdas (exlmoto)",
			filter.strip(
				filter.removeUserCasts(
					"\n@exlmoto\ttest@test ada@adasd.ru @azxczxz asd zc@ adsa@ @ asdas (@exlmoto)\n\n")
			)
		);
	}

	@Test
	public void testRemoveUserCasts() {
		assertEquals("exlmoto test@test ada@adasd.ru azxczxz asd zc@ adsa@  asdas (exlmoto)",
			filter.removeUserCasts("@exlmoto test@test ada@adasd.ru @azxczxz asd zc@ adsa@ @ asdas (@exlmoto)"));
	}
}
