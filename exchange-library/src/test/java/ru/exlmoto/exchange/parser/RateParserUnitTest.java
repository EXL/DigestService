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
	private final InnerFileHelper fileHelper = new InnerFileHelper();
	private final RateParserHelper parserHelper = new RateParserHelper();

	@Test
	public void testBankRuParser() {
		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRu.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRu.xml")));
		assertFalse(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuErrors.xml", "Windows-1251")));

		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuAlt.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuAlt.xml")));
		assertFalse(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuAltChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuAltError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankRuParser(),
			fileHelper.getFileContent("bankRuAltErrors.xml", "Windows-1251")));

		generalTests(new BankRuParser());
	}

	@Test
	public void testBankUaParser() {
		assertTrue(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUa.xml")));
		assertTrue(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUa.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUaChunk.xml")));
		assertTrue(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUaError.xml")));
		assertFalse(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUaErrors.xml")));
		assertFalse(parserHelper.process(new BankUaParser(),
			fileHelper.getFileContent("bankUaIncomplete.xml")));

		generalTests(new BankUaParser());
	}

	@Test
	public void testBankUaMirrorParser() {
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			fileHelper.getFileContent("bankUaAlt.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			fileHelper.getFileContent("bankUaAlt.xml")));
		assertFalse(parserHelper.process(new BankUaMirrorParser(),
			fileHelper.getFileContent("bankUaAltChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			fileHelper.getFileContent("bankUaAltError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankUaMirrorParser(),
			fileHelper.getFileContent("bankUaAltErrors.xml", "Windows-1251")));

		generalTests(new BankUaMirrorParser());
	}

	private void generalTests(RateParser parser) {
		assertFalse(parserHelper.process(parser, "mulfunction data"));
		assertFalse(parserHelper.process(parser, "mulfunction\ndata"));
		assertFalse(parserHelper.process(parser, "mulfunction\t\ndata"));
		assertFalse(parserHelper.process(parser, "mulfunction\t\r\ndata"));
		assertFalse(parserHelper.process(parser, "mulfunction\t\t\t\t\n\r\n\r\ndata"));
		assertFalse(parserHelper.process(parser, ""));
		assertFalse(parserHelper.process(parser, null));
	}
}
