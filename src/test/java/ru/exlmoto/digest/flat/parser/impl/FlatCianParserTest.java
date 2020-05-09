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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlatCianParserTest {
	@Autowired
	private FlatCianParser parser;

	@Autowired
	private ResourceHelper helper;

	@Test
	void testGetAvailableFlats() {
		Answer<List<Flat>> res = parser.getAvailableFlats(helper.getResourceFilePath("classpath:flat/cian.xlsx"));
		assertTrue(res.ok());
		assertEquals(res.answer().size(), 16);

		res = parser.getAvailableFlats(helper.getResourceFilePath("classpath:flat/cian-broken.xlsx"));
		assertFalse(res.ok());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);

		res = parser.getAvailableFlats(helper.getResourceFilePath("classpath:flat/cian-empty.xlsx"));
		assertFalse(res.ok());
		error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);
	}

	@Test
	void testGetFirstChunk() {
		assertEquals(parser.getFirstChunk("", "/"), "");
		assertEquals(parser.getFirstChunk("string", "/"), "string");
		assertEquals(parser.getFirstChunk("string/", "/"), "string");
		assertEquals(parser.getFirstChunk("string/asd", "/"), "string");

		assertEquals(parser.getFirstChunk("40.0", "/"), "40.0");
		assertEquals(parser.getFirstChunk("40.0/18.0", "/"), "40.0");
		assertEquals(parser.getFirstChunk("40.0/18.0/9.0", "/"), "40.0");

		assertEquals(parser.getFirstChunk("40,0", "/"), "40,0");
		assertEquals(parser.getFirstChunk("40,0/18,0", "/"), "40,0");
		assertEquals(parser.getFirstChunk("40,0/18,0/9,0", "/"), "40,0");

		assertEquals(parser.getFirstChunk("", ","), "");
		assertEquals(parser.getFirstChunk("string", ","), "string");
		assertEquals(parser.getFirstChunk("string,", ","), "string");
		assertEquals(parser.getFirstChunk("string,asd", ","), "string");
		assertEquals(parser.getFirstChunk("string, asd", ","), "string");

		assertEquals(parser.getFirstChunk("4/10", ","), "4/10");
		assertEquals(parser.getFirstChunk("4/10,Some", ","), "4/10");
		assertEquals(parser.getFirstChunk("4/10, Some text", ","), "4/10");
	}

	@Test
	void testParsePrice() {
		assertEquals(parser.parsePrice(""), "");
		assertEquals(parser.parsePrice(" "), " ");
		assertTrue(parser.parsePrice("a,asd").startsWith("a,asd "));

		String res = parser.parsePrice(" asd");
		System.out.println(res);
		assertTrue(res.startsWith("asd "));

		res = parser.parsePrice(" asd asd");
		System.out.println(res);
		assertTrue(res.startsWith("asd "));

		res = parser.parsePrice("4000000");
		System.out.println(res);
		assertTrue(res.startsWith("4,000,000 "));

		res = parser.parsePrice("4000000 rub.");
		System.out.println(res);
		assertTrue(res.startsWith("4,000,000 "));

		res = parser.parsePrice("4000000 rub., Some Long Text");
		System.out.println(res);
		assertTrue(res.startsWith("4,000,000 "));
	}

	@Test
	void testParseAddress() {
		assertEquals(parser.parseAddress(""), "");
		assertEquals(parser.parseAddress("string"), "string");
		assertEquals(parser.parseAddress("string,"), "string,");
		assertEquals(parser.parseAddress("string,string"), "string, string");
		assertEquals(parser.parseAddress("string, string"), "string, string");

		assertEquals(parser.parseAddress("NSO, Novosibirsk, ul. Unknown, 28"), "ul. Unknown, 28");
		assertEquals(parser.parseAddress("Novosibirsk, ul. Unknown, 28"), "ul. Unknown, 28");
		assertEquals(parser.parseAddress("ul. Unknown, 28"), "ul. Unknown, 28");
		assertEquals(parser.parseAddress("28"), "28");
		assertEquals(parser.parseAddress("NSO,Novosibirsk,ul. Unknown,28"), "ul. Unknown, 28");
		assertEquals(parser.parseAddress("NSO,Novosibirsk,ul.Unknown,28"), "ul.Unknown, 28");
	}

	@Test
	void testApplyAddressPatch() {
		assertEquals(parser.applyAddressPatch(""), "");
		assertEquals(parser.applyAddressPatch("string"), "string");
		assertEquals(parser.applyAddressPatch("string,string"), "string,string");
		assertEquals(parser.applyAddressPatch("string, string"), "string, string");
		assertEquals(parser.applyAddressPatch("string , string"), "string, string");

		assertEquals(parser.applyAddressPatch("улица Неизвестная,string"), "Неизвестная,string");
		assertEquals(parser.applyAddressPatch("улица Неизвестная, 30"), "Неизвестная, 30");
		assertEquals(parser.applyAddressPatch("текст, улица Неизвестная, 30"), "текст, Неизвестная, 30");

		assertEquals(parser.applyAddressPatch("ул. Неизвестная,string"), "Неизвестная,string");
		assertEquals(parser.applyAddressPatch("ул. Неизвестная, 30"), "Неизвестная, 30");
		assertEquals(parser.applyAddressPatch("текст, ул. Неизвестная, 30"), "текст, Неизвестная, 30");

		assertEquals(parser.applyAddressPatch("улица Владимира Иванова,string"), "В. Иванова,string");
		assertEquals(parser.applyAddressPatch("ул. Владимира Иванова, 30"), "В. Иванова, 30");
		assertEquals(parser.applyAddressPatch("текст, ул. Владимира Иванова, 30"), "текст, В. Иванова, 30");
	}
}
