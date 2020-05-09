package ru.exlmoto.digest.flat.parser.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlatN1ParserTest {
	@Autowired
	private FlatN1Parser parser;

	@Autowired
	private ResourceHelper helper;

	@Test
	void testGetAvailableFlats() {
		Answer<List<Flat>> res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1.json"));
		assertTrue(res.ok());
		assertEquals(res.answer().size(), 20);

		res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1-broken.json"));
		assertFalse(res.ok());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);

		res = parser.getAvailableFlats(helper.readFileToString("classpath:flat/n1-empty.json"));
		assertFalse(res.ok());
		error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);
	}

	@Test
	void testParseLink() {
		assertNull(parser.parseLink(null));
		assertEquals(parser.parseLink(""), "");
		assertEquals(parser.parseLink("test"), "test");
		assertEquals(parser.parseLink("test"), "test");
		assertEquals(parser.parseLink("  a"), "  a");
		assertEquals(parser.parseLink("//test"), "https://test");
		assertEquals(parser.parseLink("  //test"), "https://test");
		assertEquals(parser.parseLink("http://example.com"), "http://example.com");
		assertEquals(parser.parseLink("https://example.com"), "https://example.com");
	}
}
