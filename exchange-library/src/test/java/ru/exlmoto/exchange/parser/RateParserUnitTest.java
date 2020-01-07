package ru.exlmoto.exchange.parser;

import org.junit.jupiter.api.Test;

import ru.exlmoto.exchange.parser.helper.InnerFileHelper;
import ru.exlmoto.exchange.parser.helper.RateParserHelper;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.exchange.parser.impl.BankUaParser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RateParserUnitTest {
	private final InnerFileHelper f = new InnerFileHelper();
	private final RateParserHelper p = new RateParserHelper();

	@Test
	public void testBankRuParser() {
		assertTrue(p.test(new BankRuParser(), f.content("bankRu.xml", "Windows-1251")));
		assertTrue(p.test(new BankRuParser(), f.content("bankRu.xml")));
		assertFalse(p.test(new BankRuParser(), f.content("bankRuChunk.xml", "Windows-1251")));
		assertTrue(p.test(new BankRuParser(), f.content("bankRuError.xml", "Windows-1251")));
		assertFalse(p.test(new BankRuParser(), f.content("bankRuErrors.xml", "Windows-1251")));

		assertTrue(p.test(new BankRuParser(), f.content("bankRuAlt.xml", "Windows-1251")));
		assertTrue(p.test(new BankRuParser(), f.content("bankRuAlt.xml")));
		assertFalse(p.test(new BankRuParser(), f.content("bankRuAltChunk.xml", "Windows-1251")));
		assertTrue(p.test(new BankRuParser(), f.content("bankRuAltError.xml", "Windows-1251")));
		assertFalse(p.test(new BankRuParser(), f.content("bankRuAltErrors.xml", "Windows-1251")));

		generalTests(new BankRuParser());
	}

	@Test
	public void testBankUaParser() {
		assertTrue(p.test(new BankUaParser(), f.content("bankUa.xml")));
		assertTrue(p.test(new BankUaParser(), f.content("bankUa.xml", "Windows-1251")));
		assertFalse(p.test(new BankUaParser(), f.content("bankUaChunk.xml")));
		assertTrue(p.test(new BankUaParser(), f.content("bankUaError.xml")));
		assertFalse(p.test(new BankUaParser(), f.content("bankUaErrors.xml")));
		assertFalse(p.test(new BankUaParser(), f.content("bankUaIncomplete.xml")));

		generalTests(new BankUaParser());
	}

	@Test
	public void testBankUaMirrorParser() {
		assertTrue(p.test(new BankUaMirrorParser(), f.content("bankUaAlt.xml", "Windows-1251")));
		assertTrue(p.test(new BankUaMirrorParser(), f.content("bankUaAlt.xml")));
		assertFalse(p.test(new BankUaMirrorParser(), f.content("bankUaAltChunk.xml", "Windows-1251")));
		assertTrue(p.test(new BankUaMirrorParser(), f.content("bankUaAltError.xml", "Windows-1251")));
		assertFalse(p.test(new BankUaMirrorParser(), f.content("bankUaAltErrors.xml", "Windows-1251")));

		generalTests(new BankUaMirrorParser());
	}

	private void generalTests(RateParser parser) {
		assertFalse(p.test(parser, "mulfunction data"));
		assertFalse(p.test(parser, "mulfunction\ndata"));
		assertFalse(p.test(parser, "mulfunction\t\ndata"));
		assertFalse(p.test(parser, "mulfunction\t\r\ndata"));
		assertFalse(p.test(parser, "mulfunction\t\t\t\t\n\r\n\r\ndata"));
		assertFalse(p.test(parser, ""));
		assertFalse(p.test(parser, null));
	}
}
