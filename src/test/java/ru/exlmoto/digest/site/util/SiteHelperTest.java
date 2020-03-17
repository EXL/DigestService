package ru.exlmoto.digest.site.util;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SiteHelperTest {
	@Autowired
	private SiteHelper helper;

	@Test
	public void testHighlightPost() {
		assertFalse(helper.highlightPost(null, 1L));
		assertFalse(helper.highlightPost("", 1L));
		assertTrue(helper.highlightPost("0", 0L));
		assertTrue(helper.highlightPost("-1", -1L));
		assertTrue(helper.highlightPost("1", 1L));
	}

	@Test
	public void testFilterDescription() {
		System.out.println(helper.filterDescription(100, 110L, 120L, null));
		System.out.println(helper.filterDescription(100, 110L, null, null));
	}

	@Test
	public void testFilterUsername() {
		assertNull(helper.filterUsername(null, false));
		assertNull(helper.filterUsername(null, true));

		assertEquals("", helper.filterUsername("", false));
		assertEquals("", helper.filterUsername("", true));

		assertEquals("Guest", helper.filterUsername("Guest", false));
		assertEquals("Guest", helper.filterUsername("Guest", true));

		String filterWithoutAt = helper.filterUsername("@Guest", false);
		String filterWithAt = helper.filterUsername("@Guest", true);

		System.out.println(filterWithoutAt);
		System.out.println(filterWithAt);

		assertTrue(filterWithoutAt.contains("member-user"));
		assertTrue(filterWithAt.contains("member-user"));

		filterWithoutAt = helper.filterUsername("@yakimka", false);
		assertTrue(filterWithoutAt.contains("member-moderator"));

		filterWithoutAt = helper.filterUsername("@exlmoto", true);
		assertTrue(filterWithoutAt.contains("member-administrator"));
	}

	@Test
	public void testFilterUsernameTooLong() {
		System.out.println(helper.filterUsername("@longlonglonglonglonglonglonglonglong_username", false));
		System.out.println(helper.filterUsername("@longlonglonglonglonglonglonglonglong_username", true));
		System.out.println(helper.filterUsername("longlonglonglonglonglonglonglonglong_name", false));
		System.out.println(helper.filterUsername("longlonglonglonglonglonglonglonglong_name", true));
	}

	@Test
	public void testFilterAvatarLink() {
		assertNull(helper.filterAvatarLink(null));
		assertEquals("", helper.filterAvatarLink(""));

		System.out.println(helper.filterAvatarLink("://"));
		System.out.println(helper.filterAvatarLink("://exl"));
		System.out.println(helper.filterAvatarLink("://exlmoto.ru"));
		System.out.println(helper.filterAvatarLink("://exlmoto.ru/test.jpg"));
		System.out.println(helper.filterAvatarLink("http://exlmoto.ru/test.jpg"));
		System.out.println(helper.filterAvatarLink("https://exlmoto.ru/test.jpg"));
	}

	@Test
	public void testFilterGroup() {
		System.out.println(helper.filterGroup(null, null));
		System.out.println(helper.filterGroup("", null));
		System.out.println(helper.filterGroup("Guest", null));
		System.out.println(helper.filterGroup("@Guest", null));
		System.out.println(helper.filterGroup("@yakimka", null));
		System.out.println(helper.filterGroup("@exlmoto", Locale.forLanguageTag("ru")));
	}

	@Test
	public void testFilterDateAndTime() {
		System.out.println(helper.filterDateAndTime(0L, null));
		System.out.println(helper.filterDateAndTime(1584464518L, Locale.forLanguageTag("ru")));
		System.out.println(helper.filterDateAndTime(15844645181L, Locale.forLanguageTag("ru")));
		System.out.println(helper.filterDateAndTime(158446451821L, Locale.forLanguageTag("ru")));
	}

	@Test
	public void testFilterDigestOrder() {
		assertNull(helper.filterDigestOrder(null, null));
		assertNull(helper.filterDigestOrder(null, ""));
		assertNull(helper.filterDigestOrder(null, "st"));

		assertEquals("", helper.filterDigestOrder("", null));
		assertEquals("", helper.filterDigestOrder("", ""));
		assertEquals("", helper.filterDigestOrder("", "st"));

		System.out.println(helper.filterDigestOrder("Test @exlmoto https://exlmoto.ru", null));
		System.out.println(helper.filterDigestOrder("Test @exlmoto https://exlmoto.ru", ""));

		System.out.println(helper.filterDigestOrder("Test @exlmoto https://exlmoto.ru", ""));
		System.out.println(helper.filterDigestOrder("Test @exlmoto https://exlmoto.ru", "st"));
	}

	@Test
	public void testActivateUsers() {
		assertNull(helper.activateUsers(null));
		assertEquals("", helper.activateUsers(""));
		assertEquals("Test", helper.activateUsers("Test"));

		System.out.println(helper.activateUsers("Test @u"));
		System.out.println(helper.activateUsers("Test @u @z"));

		System.out.println(helper.activateUsers("@exlmoto test@test"));
		System.out.println(helper.activateUsers("ada@adasd.ru"));
		System.out.println(helper.activateUsers("@azxczxz asd zc@"));
		System.out.println(helper.activateUsers("adsa@ @ asdas (@exlmoto)"));
		System.out.println(helper.activateUsers("@ZorgeR '@ZorgeR"));
		System.out.println(helper.activateUsers("@@mb@v06 @@mbv06"));
		System.out.println(helper.activateUsers("Test <@exlmoto and '@exlmoto"));
		System.out.println(helper.activateUsers("Test >@exlmoto and end."));
		System.out.println(helper.activateUsers("Test email@exlmoto.ru"));
		System.out.println(helper.activateUsers("Test _@exlmoto."));
	}

	@Test
	public void testActivateLinks() {
		assertNull(helper.activateLinks(null));
		assertEquals("", helper.activateLinks(""));
		assertEquals("Test", helper.activateLinks("Test"));

		System.out.println(helper.activateLinks("Test http:/"));
		System.out.println(helper.activateLinks("Test https:/"));
		System.out.println(helper.activateLinks("Test ://"));
		System.out.println(helper.activateLinks("Test //exlmoto.ru/"));

		System.out.println(helper.activateLinks("Test http://exlmoto.ru"));
		System.out.println(helper.activateLinks("Test https://exlmoto.ru"));
		System.out.println(helper.activateLinks("Test https://exlmoto"));
		System.out.println(helper.activateLinks("Test http://"));
		System.out.println(helper.activateLinks("Test https://"));
		System.out.println(helper.activateLinks("Test https://exlmoto.ru/manual#digest"));
		System.out.println(helper.activateLinks("Test #digest"));

		System.out.println(helper.activateLinks("Test https://exlmoto.ru https://exlmoto.ru"));
		System.out.println(helper.activateLinks("Test https://exlmoto.ru tg://t.me/exlmoto file:///usr/local/"));

		System.out.println(helper.activateLinks("Test https://exlmotoexlmotoexlmotoexlmotoexlmotoe" +
			"xlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmotoexlmoto.ru"));

		// Unicode link.
		System.out.println(helper.activateLinks("Test https://президент.рф"));
	}

	@Test
	public void testActivateHighlight() {
		assertNull(helper.activateHighlight(null, null));
		assertNull(helper.activateHighlight(null, ""));
		assertNull(helper.activateHighlight(null, "test"));

		assertEquals("", helper.activateHighlight("", null));
		assertEquals("", helper.activateHighlight("", ""));
		assertEquals("", helper.activateHighlight("", "test"));

		assertEquals("Test", helper.activateHighlight("Test", "NaN"));

		System.out.println(helper.activateHighlight("is there is some text here", "is"));
		System.out.println(helper.activateHighlight("is there is some text here", "text"));
		System.out.println(helper.activateHighlight("is there is some text here", "here"));
		System.out.println("---");

		System.out.println(helper.activateHighlight("is there is some text here", " "));
		System.out.println(helper.activateHighlight("is there is some text here", "e"));
		System.out.println(helper.activateHighlight("is there is some text here", "as"));
		System.out.println("---");

		System.out.println(helper.activateHighlight("is @there is some text here", "here"));
		System.out.println(helper.activateHighlight("is http://there is some text here", "here"));
		System.out.println(helper.activateHighlight("is https://there is some text here", "here"));
		System.out.println("---");
	}

	@Test
	public void testFilterDigestCount() {
		System.out.println(helper.filterDigestCount(100L, 110L, null));
		System.out.println(helper.filterDigestCount(120L, 130L, Locale.forLanguageTag("ru")));
	}

	@Test
	public void testGetCurrentPage() {
		assertEquals(5, helper.getCurrentPage(null, 5));
		assertEquals(5, helper.getCurrentPage("", 5));
		assertEquals(5, helper.getCurrentPage("0", 5));
		assertEquals(5, helper.getCurrentPage("-1", 5));
		assertEquals(1, helper.getCurrentPage("1", 5));
		assertEquals(2, helper.getCurrentPage("2", 5));
		assertEquals(4, helper.getCurrentPage("4", 5));
		assertEquals(5, helper.getCurrentPage("6", 5));
		assertEquals(5, helper.getCurrentPage("1231231231214221124", 5));
		assertEquals(5, helper.getCurrentPage("NaN", 5));
		assertEquals(5, helper.getCurrentPage("<a href", 5));
	}

	@Test
	public void testGetLong() {
		assertNull(helper.getLong(null));
		assertNull(helper.getLong(""));
		assertEquals(0L, helper.getLong("0"));
		assertEquals(-1L, helper.getLong("-1"));
		assertEquals(1L, helper.getLong("1"));
		assertEquals(1231231231214221124L, helper.getLong("1231231231214221124"));
		assertNull(helper.getLong("123123123121422112411111"));
		assertNull(helper.getLong("NaN"));
		assertNull(helper.getLong("<a href"));
	}

	@Test
	public void testGetPageCount() {
		assertEquals(1, helper.getPageCount(-1L, 0));
		assertEquals(1, helper.getPageCount(-1L, 1));
		assertEquals(1, helper.getPageCount(-1L, 10));
		assertEquals(1, helper.getPageCount(0L, 0));
		assertEquals(1, helper.getPageCount(0L, 1));
		assertEquals(1, helper.getPageCount(0L, 10));

		assertEquals(1, helper.getPageCount(1L, 0));
		assertEquals(1, helper.getPageCount(1L, 1));
		assertEquals(1, helper.getPageCount(1L, 10));

		assertEquals(2, helper.getPageCount(2L, 0));
		assertEquals(2, helper.getPageCount(2L, 1));
		assertEquals(1, helper.getPageCount(2L, 10));

		assertEquals(10, helper.getPageCount(10L, 0));
		assertEquals(10, helper.getPageCount(10L, 1));
		assertEquals(2, helper.getPageCount(10L, 5));
		assertEquals(4, helper.getPageCount(10L, 3));
		assertEquals(1, helper.getPageCount(10L, 10));
		assertEquals(1, helper.getPageCount(10L, 20));

		assertEquals(Integer.MAX_VALUE, helper.getPageCount(500000000000000000L, 0));
		assertEquals(1, helper.getPageCount(-500000000000000000L, 0));
	}

	@Test
	public void testDropAt() {
		assertThrows(NullPointerException.class, () -> helper.dropAt(null));
		assertEquals("", helper.dropAt(""));
		assertEquals("", helper.dropAt("@"));
		assertEquals("a", helper.dropAt("@a"));
		assertEquals("username", helper.dropAt("username"));
		assertEquals("username", helper.dropAt("@username"));
	}
}
