/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.util.filter;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class FilterHelperTest {
	@Autowired
	private FilterHelper filter;

	@Test
	public void testRemoveHtmlTags() {
		assertThrows(NullPointerException.class, () -> filter.removeHtmlTags(null));

		assertEquals("test &lt;&lt; test", filter.removeHtmlTags("test &#60;&#60; test"));
		assertNotEquals("test << test", filter.removeHtmlTags("test &#60;&#60; test"));

		assertEquals("test text test", filter.removeHtmlTags("test <b>text</b> test"));
		assertEquals("test text test", filter.removeHtmlTags("test <b id=\"a\">text</b> test"));
		assertEquals("test text test", filter.removeHtmlTags("test <gibberish> text test</..."));
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

		System.out.println(filter.getDateFromTimeStamp("dd.MM.yyyy EEEE", 1580520624L));
		System.out.println(filter.getDateFromTimeStamp("dd-MM-yyyy EEEE", Locale.forLanguageTag("en"), 1580585762L));
		System.out.println(filter.getDateFromTimeStamp("dd-MM-yyyy EEEE", Locale.forLanguageTag("fr"), 1580585762L));
		System.out.println(filter.getDateFromTimeStamp("dd.MMM.yyyy EEEE", Locale.forLanguageTag("ru"), 1580520624L));
	}

	@Test
	public void testGetCurrentUnixTime() {
		String value = String.valueOf(filter.getCurrentUnixTime());
		assertEquals(10, value.length());
		System.out.println("Unix Time: " + value);
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
		assertNull(filter.strip(null));
		assertEquals("", filter.strip(""));
		assertEquals("a a@ as (exl)", filter.strip(filter.removeUserCasts("\na\ta@ @ as (@exl)\n\n")));
		assertEquals("Check this a! A as asd!", filter.strip("\t\n\n\nCheck this   a! A  as\t asd!"));
	}

	@Test
	public void testRemoveUserCasts() {
		assertEquals("exlmoto test@test", filter.removeUserCasts("@exlmoto test@test"));
		assertEquals("ada@adasd.ru", filter.removeUserCasts("ada@adasd.ru"));
		assertEquals("gibberish asd zc@", filter.removeUserCasts("@gibberish asd zc@"));
		assertEquals("cat@  cat (exlmoto)", filter.removeUserCasts("cat@ @ cat (@exlmoto)"));
		assertEquals("ZorgeR 'ZorgeR", filter.removeUserCasts("@ZorgeR '@ZorgeR"));
		assertEquals("mb@v06 mbv06", filter.removeUserCasts("@@mb@v06 @@mbv06"));
		assertEquals("Test <exlmoto and 'exlmoto", filter.removeUserCasts("Test <@exlmoto and '@exlmoto"));
		assertEquals("Test exlmoto", filter.removeUserCasts("Test @exlmoto"));
		assertEquals("Test exlmoto.", filter.removeUserCasts("Test @exlmoto."));
		assertEquals("Test _@exlmoto", filter.removeUserCasts("Test _@exlmoto"));
		assertEquals("Test _@exlmoto.", filter.removeUserCasts("Test _@exlmoto."));
		assertEquals("Test email@exlmoto.ru", filter.removeUserCasts("Test email@exlmoto.ru"));
		assertEquals("Test >exlmoto and end.", filter.removeUserCasts("Test >@exlmoto and end."));
		assertEquals("Nice https://v.c/@exlmoto here.", filter.removeUserCasts("Nice https://v.c/@exlmoto here."));
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

	@Test
	public void testActivateLink() {
		assertNull(filter.activateLink(null, null));
		assertNull(filter.activateLink(null, ""));
		assertNull(filter.activateLink(null, " "));

		assertThat(filter.activateLink("", null)).isBlank();
		assertThat(filter.activateLink(" ", null)).isBlank();
		assertThat(filter.activateLink("", "")).isBlank();
		assertThat(filter.activateLink("", " ")).isBlank();

		String linkWoTitle = "<a href=\"https://exlmoto.ru\" target=\"_blank\">www.exlmoto.ru</a>";
		String linkWTitle = "<a href=\"https://exlmoto.ru\" target=\"_blank\" title=\"exlmoto\">www.exlmoto.ru</a>";

		assertEquals(linkWoTitle, filter.activateLink("https://exlmoto.ru", "www.exlmoto.ru"));
		assertEquals(linkWTitle, filter.activateLink("https://exlmoto.ru", "www.exlmoto.ru", "exlmoto"));
	}

	@Test
	public void testGetSiteUrlFromLink() {
		assertNull(filter.getSiteUrlFromLink(null));
		assertThat(filter.getSiteUrlFromLink("")).isBlank();
		assertThat(filter.getSiteUrlFromLink(" ")).isBlank();

		assertEquals("https://forum.motofan.ru",
			filter.getSiteUrlFromLink("https://forum.motofan.ru/lastpost_json.php"));
		assertEquals("https://www.forum.motofan.ru",
			filter.getSiteUrlFromLink("https://www.forum.motofan.ru/lastpost_json.php"));

		assertEquals("Malfunction URL", filter.getSiteUrlFromLink("Malfunction URL"));
	}

	@Test
	public void testReplaceLast() {
		assertNull(filter.replaceLast(null, null, null));
		assertEquals(" ", filter.replaceLast(" ", null, null));
		assertNull(filter.replaceLast(null, "(", " ("));
		assertEquals("", filter.replaceLast("", "(", " ("));
		assertEquals(" ", filter.replaceLast(" ", "(", " ("));
		assertEquals("test", filter.replaceLast("test", "(", " ("));
		assertEquals("test (", filter.replaceLast("test(", "(", " ("));
		assertEquals("J()KER (28)", filter.replaceLast("J()KER(28)", "(", " ("));
		assertEquals("* J()KER (28)", filter.replaceLast("* J()KER(28)", "(", " ("));
	}
}
