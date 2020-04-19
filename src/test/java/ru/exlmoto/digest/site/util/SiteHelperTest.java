/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.site.util;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.service.DatabaseService;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SiteHelperTest {
	@Autowired
	private SiteHelper helper;

	@Autowired
	private DatabaseService service;

	@Test
	public void testGetUsers() {
		assertFalse(helper.getUsers(service, null, true, null).isEmpty());
		assertFalse(helper.getUsers(service, null, false, null).isEmpty());
		assertFalse(helper.getUsers(service, "", true, null).isEmpty());
		assertFalse(helper.getUsers(service, "", false, null).isEmpty());

		assertFalse(helper.getUsers(service, "name", true, null).isEmpty());
		assertFalse(helper.getUsers(service, "id", false, null).isEmpty());
		assertFalse(helper.getUsers(service, "group", true, null).isEmpty());
		assertFalse(helper.getUsers(service, "post", false, null).isEmpty());
	}

	@Test
	public void testGetPostsMethods() {
		assertTrue(helper.getPosts(null, null, 0, null, null).isEmpty());
		assertTrue(helper.getPostsSearch(null, 0, null, null, null).isEmpty());
	}

	@Test
	public void testGetMotofanTitleSearch() {
		System.out.println(helper.getMotofanTitleSearch(null, null, null));
		System.out.println(helper.getMotofanTitleSearch(null, "", null));
		System.out.println(helper.getMotofanTitleSearch(null, "No-No-No", null));
		System.out.println("---");

		BotDigestUserEntity botDigestUserEntity = new BotDigestUserEntity();
		botDigestUserEntity.setUsername("@username");
		System.out.println(helper.getMotofanTitleSearch(botDigestUserEntity, null, null));
		System.out.println(helper.getMotofanTitleSearch(botDigestUserEntity, "", null));
		System.out.println(helper.getMotofanTitleSearch(botDigestUserEntity, "No-No-No", null));
	}

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
		System.out.println(helper.activateUsers("@gibberish asd zc@"));
		System.out.println(helper.activateUsers("cat@ @ cat (@exlmoto)"));
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
	}

	@Test
	public void testActivateLinksSpecialCases() {
		// Unicode link.
		System.out.println(helper.activateLinks("Test https://президент.рф"));

		// WWW link.
		System.out.println(helper.activateLinks("Test www.exlmoto.ru"));
		System.out.println(helper.activateLinks("Test www1.exlmoto.ru"));
		System.out.println(helper.activateLinks("Test www2.exlmoto.ru"));

		// E-Mail.
		System.out.println(helper.activateLinks("Test @exlmoto"));
		System.out.println(helper.activateLinks("Test test@exlmoto"));
		System.out.println(helper.activateLinks("Test test@exlmoto.ru"));
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
	public void testGetCurrentPageDesc() {
		assertEquals(1, helper.getCurrentPage(null));
		assertEquals(1, helper.getCurrentPage(""));
		assertEquals(1, helper.getCurrentPage("0"));
		assertEquals(1, helper.getCurrentPage("-1"));
		assertEquals(1, helper.getCurrentPage("1"));
		assertEquals(2, helper.getCurrentPage("2"));
		assertEquals(4, helper.getCurrentPage("4"));
		assertEquals(6, helper.getCurrentPage("6"));
		assertEquals(1, helper.getCurrentPage("1231231231214221124"));
		assertEquals(1, helper.getCurrentPage("NaN"));
		assertEquals(1, helper.getCurrentPage("<a href"));
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
		assertNull(helper.dropAt(null));
		assertEquals("", helper.dropAt(""));
		assertEquals("", helper.dropAt("@"));
		assertEquals("a", helper.dropAt("@a"));
		assertEquals("username", helper.dropAt("username"));
		assertEquals("username", helper.dropAt("@username"));
	}

	@Test
	public void testChopQuery() {
		assertNull(helper.chopQuery(null));
		assertEquals("", helper.chopQuery(""));
		assertEquals("test", helper.chopQuery("test"));

		System.out.println(helper.chopQuery("longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglong" +
			"longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonga"));
	}

	@Test
	public void testCopyRateValues() {
		ExchangeRateEntity first = new ExchangeRateEntity();
		ExchangeRateEntity second = new ExchangeRateEntity();

		String date = "08/01/2020";
		String usd = "78";
		String eur = "79.5600";
		String gbp = "13456.45566";
		String cny = "423.0";
		String rub = "333.0";
		String uah = "1866.45565";
		String byn = "184.0";
		String kzt = "1.1334231";
		String gold = "14124.9";
		String silver = "13213.1";
		String platinum = "1234.1";
		String palladium = "55534.1";
		String prevUsd = "88.100000000000000001";
		String prevEur = "9.5600";
		String prevGbp = "3456.45566";
		String prevCny = "23.0";
		String prevRub = "33.0";
		String prevUah = "866.45565";
		String prevByn = "84.0";
		String prevKzt = "0.1334231";
		String prevGold = "4124.9";
		String prevSilver = "3213.1";
		String prevPlatinum = "234.1";
		String prevPalladium = "5534.1";

		first.setDate(date);
		first.setUsd(new BigDecimal(usd));
		first.setEur(new BigDecimal(eur));
		first.setGbp(new BigDecimal(gbp));
		first.setCny(new BigDecimal(cny));
		first.setRub(new BigDecimal(rub));
		first.setUah(new BigDecimal(uah));
		first.setByn(new BigDecimal(byn));
		first.setKzt(new BigDecimal(kzt));
		first.setGold(new BigDecimal(gold));
		first.setSilver(new BigDecimal(silver));
		first.setPlatinum(new BigDecimal(platinum));
		first.setPalladium(new BigDecimal(palladium));
		first.setPrevUsd(new BigDecimal(prevUsd));
		first.setPrevEur(new BigDecimal(prevEur));
		first.setPrevGbp(new BigDecimal(prevGbp));
		first.setPrevCny(new BigDecimal(prevCny));
		first.setPrevRub(new BigDecimal(prevRub));
		first.setPrevUah(new BigDecimal(prevUah));
		first.setPrevByn(new BigDecimal(prevByn));
		first.setPrevKzt(new BigDecimal(prevKzt));
		first.setPrevGold(new BigDecimal(prevGold));
		first.setPrevSilver(new BigDecimal(prevSilver));
		first.setPrevPlatinum(new BigDecimal(prevPlatinum));
		first.setPrevPalladium(new BigDecimal(prevPalladium));

		helper.copyRateValues(first, second);

		assertEquals(date, second.getDate());
		assertEquals(usd, second.getUsd().toPlainString());
		assertEquals(eur, second.getEur().toPlainString());
		assertEquals(gbp, second.getGbp().toPlainString());
		assertEquals(cny, second.getCny().toPlainString());
		assertEquals(rub, second.getRub().toPlainString());
		assertEquals(uah, second.getUah().toPlainString());
		assertEquals(byn, second.getByn().toPlainString());
		assertEquals(kzt, second.getKzt().toPlainString());
		assertEquals(gold, second.getGold().toPlainString());
		assertEquals(silver, second.getSilver().toPlainString());
		assertEquals(platinum, second.getPlatinum().toPlainString());
		assertEquals(palladium, second.getPalladium().toPlainString());
		assertEquals(prevUsd, second.getPrevUsd().toPlainString());
		assertEquals(prevEur, second.getPrevEur().toPlainString());
		assertEquals(prevGbp, second.getPrevGbp().toPlainString());
		assertEquals(prevCny, second.getPrevCny().toPlainString());
		assertEquals(prevRub, second.getPrevRub().toPlainString());
		assertEquals(prevUah, second.getPrevUah().toPlainString());
		assertEquals(prevByn, second.getPrevByn().toPlainString());
		assertEquals(prevKzt, second.getPrevKzt().toPlainString());
		assertEquals(prevGold, second.getPrevGold().toPlainString());
		assertEquals(prevSilver, second.getPrevSilver().toPlainString());
		assertEquals(prevPlatinum, second.getPrevPlatinum().toPlainString());
		assertEquals(prevPalladium, second.getPrevPalladium().toPlainString());
	}

	@Test
	public void testCopyRateValuesNullable() {
		assertNull(helper.copyRateValues(null, null));
		assertNull(helper.copyRateValues(null, new ExchangeRateEntity()));
		assertNull(helper.copyRateValues(new ExchangeRateEntity(), null));
	}

	@Test
	public void testGenerateAdminLink() {
		String res = helper.generateAdminLink();
		assertTrue(res.contains("<a href=\"//t.me/exlmoto\" title=\"@exlmoto\" target=\"_blank\">exlmoto</a>"));
		System.out.println(res);
	}
}
