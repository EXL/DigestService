package ru.exlmoto.digest.site.util;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class SiteHelperTest {
	@Autowired
	private SiteHelper helper;

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
}
