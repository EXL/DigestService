package ru.exlmoto.digest.exchange.parser;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.util.file.ResourceHelper;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RateParserUnitTest {
	private final RateParserHelper parserHelper = new RateParserHelper();

	private static class RateParserHelper {
		private final ResourceHelper resourceHelper = new ResourceHelper();

		public boolean process(RateParser parser, String content) {
			boolean result = parser.parse(content);
			parser.logParsedValues();
			return result;
		}

		public String fileContent(String name) {
			return resourceHelper.readFileToString("classpath:exchange/" + name);
		}

		public String fileContent(String name, String charset) {
			return resourceHelper.readFileToString("classpath:exchange/" + name, Charset.forName(charset));
		}
	}

	@Test
	public void testBankRuParser() {
		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRu.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRu.xml")));
		assertFalse(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuErrors.xml", "Windows-1251")));

		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuAlt.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuAlt.xml")));
		assertFalse(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuAltChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuAltError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankRuParser(),
			parserHelper.fileContent("bankRuAltErrors.xml", "Windows-1251")));

		generalTests(new BankRuParser());
	}

	@Test
	public void testBankUaParser() {
		assertTrue(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUa.xml")));
		assertTrue(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUa.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUaChunk.xml")));
		assertTrue(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUaError.xml")));
		assertFalse(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUaErrors.xml")));
		assertFalse(parserHelper.process(new BankUaParser(),
			parserHelper.fileContent("bankUaIncomplete.xml")));

		generalTests(new BankUaParser());
	}

	@Test
	public void testBankUaMirrorParser() {
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			parserHelper.fileContent("bankUaAlt.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			parserHelper.fileContent("bankUaAlt.xml")));
		assertFalse(parserHelper.process(new BankUaMirrorParser(),
			parserHelper.fileContent("bankUaAltChunk.xml", "Windows-1251")));
		assertTrue(parserHelper.process(new BankUaMirrorParser(),
			parserHelper.fileContent("bankUaAltError.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankUaMirrorParser(),
			parserHelper.fileContent("bankUaAltErrors.xml", "Windows-1251")));

		generalTests(new BankUaMirrorParser());
	}

	@Test
	public void testBankByParser() {
		assertTrue(parserHelper.process(new BankByParser(),
			parserHelper.fileContent("bankBy.xml")));
		assertTrue(parserHelper.process(new BankByParser(),
			parserHelper.fileContent("bankBy.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankByParser(),
			parserHelper.fileContent("bankByChunk.xml")));
		assertTrue(parserHelper.process(new BankByParser(),
			parserHelper.fileContent("bankByError.xml")));
		assertFalse(parserHelper.process(new BankByParser(),
			parserHelper.fileContent("bankByErrors.xml")));

		generalTests(new BankByParser());
	}

	@Test
	public void testBankKzParser() {
		assertTrue(parserHelper.process(new BankKzParser(),
			parserHelper.fileContent("bankKz.xml")));
		assertTrue(parserHelper.process(new BankKzParser(),
			parserHelper.fileContent("bankKz.xml", "Windows-1251")));
		assertFalse(parserHelper.process(new BankKzParser(),
			parserHelper.fileContent("bankKzChunk.xml")));
		assertTrue(parserHelper.process(new BankKzParser(),
			parserHelper.fileContent("bankKzError.xml")));
		assertFalse(parserHelper.process(new BankKzParser(),
			parserHelper.fileContent("bankKzErrors.xml")));

		generalTests(new BankKzParser());
	}

	@Test
	public void testMetalRuParser() {
		assertTrue(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalRu.html")));
		assertTrue(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalRu.html", "Windows-1251")));
		assertFalse(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalRuChunk.html")));
		assertTrue(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalRuError.html")));
		assertFalse(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalRuErrors.html")));
		assertFalse(parserHelper.process(new MetalRuParser(),
			parserHelper.fileContent("metalOtherSite.html")));

		generalTests(new MetalRuParser());
	}

	@Test
	public void testMetalRuMirrorParser() {
		assertTrue(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalRuAlt.html")));
		assertTrue(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalRuAlt.html", "Windows-1251")));
		assertFalse(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalRuAltChunk.html")));
		assertTrue(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalRuAltError.html")));
		assertFalse(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalRuAltErrors.html")));
		assertFalse(parserHelper.process(new MetalRuMirrorParser(),
			parserHelper.fileContent("metalOtherSite.html")));

		generalTests(new MetalRuMirrorParser());
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
