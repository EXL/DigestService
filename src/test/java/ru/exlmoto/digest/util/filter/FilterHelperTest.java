package ru.exlmoto.digest.util.filter;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilterHelperTest {
	@Autowired
	private FilterHelper filter;

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
	public void testEscapeTags() {
		assertEquals("&#60;@exlmoto", filter.escapeTags("<@exlmoto"));
		assertEquals("&#62;@exlmoto", filter.escapeTags(">@exlmoto"));
		assertEquals("&#60;&#62;@exlmoto", filter.escapeTags("<>@exlmoto"));
		assertEquals("&#60;@exlmoto&#62;", filter.escapeTags("<@exlmoto>"));
		assertEquals("&#60;&#60;&#62;&#60;@exlmoto&#62;&#60;&#60;", filter.escapeTags("<<><@exlmoto><<"));
	}

	@Test
	public void testStrip() {
		assertEquals("a a@ as (exl)", filter.strip(filter.removeUserCasts("\na\ta@ @ as (@exl)\n\n")));
		assertEquals("Check this a! A as asd!", filter.strip("\t\n\n\nCheck this   a! A  as\t asd!"));
	}

	@Test
	public void testRemoveUserCasts() {
		assertEquals("exlmoto test@test", filter.removeUserCasts("@exlmoto test@test"));
		assertEquals("ada@adasd.ru", filter.removeUserCasts("ada@adasd.ru"));
		assertEquals("azxczxz asd zc@", filter.removeUserCasts("@azxczxz asd zc@"));
		assertEquals("adsa@  asdas (exlmoto)", filter.removeUserCasts("adsa@ @ asdas (@exlmoto)"));
		assertEquals("ZorgeR 'ZorgeR", filter.removeUserCasts("@ZorgeR '@ZorgeR"));
		assertEquals("mb@v06 mbv06", filter.removeUserCasts("@@mb@v06 @@mbv06"));
		assertEquals("Test <exlmoto and 'exlmoto", filter.removeUserCasts("Test <@exlmoto and '@exlmoto"));
		assertEquals("Test exlmoto", filter.removeUserCasts("Test @exlmoto"));
		assertEquals("Test exlmoto.", filter.removeUserCasts("Test @exlmoto."));
		assertEquals("Test _@exlmoto", filter.removeUserCasts("Test _@exlmoto"));
		assertEquals("Test _@exlmoto.", filter.removeUserCasts("Test _@exlmoto."));
		assertEquals("Test _@exlmoto.", filter.removeUserCasts("Test _@exlmoto."));
		assertEquals("Test email@exlmoto.ru", filter.removeUserCasts("Test email@exlmoto.ru"));
		assertEquals("Test >exlmoto and end.", filter.removeUserCasts("Test >@exlmoto and end."));
	}
	
	@Test
	public void testArrangeString() {
		assertEquals("i     ", filter.arrangeString("i", 6));
		assertEquals("id    ", filter.arrangeString("id", 6));
		assertEquals("post  ", filter.arrangeString("post", 6));
		assertEquals("post-post", filter.arrangeString("post-post", 6));
		assertEquals("id", filter.arrangeString("id", 0));
		assertEquals("id", filter.arrangeString("id", 1));
		assertEquals("id", filter.arrangeString("id", 2));
		assertEquals("id ", filter.arrangeString("id", 3));
		assertEquals("id", filter.arrangeString("id", -1));
	}

	@Test
	public void testEllipsisString() {
		assertEquals("usern...",
			filter.ellipsisString("username", 6, "...", 1, true));
		assertEquals("...rname",
			filter.ellipsisString("username", 6, "...", -1, true));

		assertEquals("user...",
			filter.ellipsisString("username", 5, "...", 1, true));
		assertEquals("...name",
			filter.ellipsisString("username", 5, "...", -1, true));

		assertEquals("Laser   ",
			filter.ellipsisString("Laser", 8, "...", 1, true));
		assertEquals("Laser   ",
			filter.ellipsisString("Laser", 8, "...", -1, true));
		assertEquals("Laser",
			filter.ellipsisString("Laser", 8, "...", 1, false));
		assertEquals("Laser",
			filter.ellipsisString("Laser", 8, "...", -1, false));
		assertEquals("Laser   ",
			filter.ellipsisString("Laser", 8, "...", 0, true));
		assertEquals("Laser",
			filter.ellipsisString("Laser", 8, "...", 0, false));

		assertEquals("Laser At...",
			filter.ellipsisString("Laser Attack", 9, "...", 1, true));
		assertEquals("...r Attack",
			filter.ellipsisString("Laser Attack", 9, "...", -1, true));
		assertEquals("Laser At...",
			filter.ellipsisString("Laser Attack", 9, "...", 1, false));
		assertEquals("...r Attack",
			filter.ellipsisString("Laser Attack", 9, "...", -1, false));
		assertEquals("Lase...tack",
			filter.ellipsisString("Laser Attack", 9, "...", 0, true));
		assertEquals("Lase...tack",
			filter.ellipsisString("Laser Attack", 9, "...", 0, false));

		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", 1, true));
		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", -1, true));
		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", 1, false));
		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", -1, false));
		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", 0, true));
		assertEquals("...",
			filter.ellipsisString("Laser Attack", 1, "...", 0, false));

		assertEquals("L...k",
			filter.ellipsisString("Laser Attack", 2, "...", 0, true));
		assertEquals("L...k",
			filter.ellipsisString("Laser Attack", 2, "...", 0, false));
		assertEquals("L...k",
			filter.ellipsisString("Laser Attack", 3, "...", 0, true));
		assertEquals("L...k",
			filter.ellipsisString("Laser Attack", 3, "...", 0, false));
		assertEquals("La...ck",
			filter.ellipsisString("Laser Attack", 4, "...", 0, true));
		assertEquals("La...ck",
			filter.ellipsisString("Laser Attack", 4, "...", 0, false));
		assertEquals("La...ck",
			filter.ellipsisString("Laser Attack", 5, "...", 0, true));
		assertEquals("La...ck",
			filter.ellipsisString("Laser Attack", 5, "...", 0, false));
		assertEquals("Las...ack",
			filter.ellipsisString("Laser Attack", 6, "...", 0, true));
		assertEquals("Las...ack",
			filter.ellipsisString("Laser Attack", 6, "...", 0, false));

		assertEquals("Laser Attack",
			filter.ellipsisString("Laser Attack", 0, "...", 1, true));
		assertEquals("Laser Attack",
			filter.ellipsisString("Laser Attack", 0, "...", -1, true));

		assertEquals("Laser Attack",
			filter.ellipsisString("Laser Attack", -1, "...", 1, true));
		assertEquals("Laser Attack",
			filter.ellipsisString("Laser Attack", -1, "...", -1, true));
	}
}
