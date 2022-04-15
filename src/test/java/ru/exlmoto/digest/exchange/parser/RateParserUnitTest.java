/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.exchange.parser;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BitcoinParser;
import ru.exlmoto.digest.exchange.parser.additional.RateAliParser;
import ru.exlmoto.digest.exchange.parser.additional.RateAliHelpixParser;
import ru.exlmoto.digest.exchange.parser.additional.RateRbcParser;
import ru.exlmoto.digest.util.file.ResourceHelper;

import java.math.BigDecimal;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateParserUnitTest {
	private final RateParserHelper parserHelper = new RateParserHelper();

	private static class RateParserHelper {
		private final ResourceHelper resourceHelper = new ResourceHelper();

		public boolean process(GeneralParser parser, String content) {
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
			parserHelper.fileContent("metalOtherSite.html", "ISO-8859-1")));

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

	@Test
	public void testBitcoinParser() {
		assertTrue(parserHelper.process(new BitcoinParser(), parserHelper.fileContent("bitcoinRates.json")));
		assertFalse(parserHelper.process(new BitcoinParser(), parserHelper.fileContent("bitcoinRatesError.json")));

		generalTests(new BitcoinParser());
	}

	@Test
	public void testAliexpressParser() {
		assertTrue(parserHelper.process(new RateAliParser(), parserHelper.fileContent("currencyAli.json")));
		assertFalse(parserHelper.process(new RateAliParser(), parserHelper.fileContent("currencyAliError.json")));

		generalTests(new RateAliParser());
	}

	@Test
	public void testAliexpressHelpixParser() {
		assertTrue(parserHelper.process(new RateAliHelpixParser(), parserHelper.fileContent("helpix.html")));
		assertFalse(parserHelper.process(new RateAliHelpixParser(), parserHelper.fileContent("helpixError.html")));

		generalTests(new RateAliHelpixParser());
	}

	@Test
	public void testRbcParser() {
		assertTrue(parserHelper.process(new RateRbcParser(), parserHelper.fileContent("currencyRbc.json")));
		assertFalse(parserHelper.process(new RateRbcParser(), parserHelper.fileContent("currencyRbcError.json")));

		generalTests(new RateRbcParser());
	}

	@Test
	public void testChopContent() {
		assertEquals(
			new BankRuParser().chopContent("1234567890"),
			"1234567890"
		);
		assertEquals(
			new BankRuParser().chopContent("12345678901234567890123456789012345678901234567890"),
			"12345678901234567890123456789012345678901234567890"
		);
		assertEquals(
			new BankRuParser().chopContent("123456789012345678901234567890123456789012345678901"),
			"12345678901234567890123456789012345678901234567890"
		);
	}

	@Test
	public void testFormatDate() {
		assertEquals(new BankRuParser().formatDate("2022-11-19"), "19.11");
	}

	@Test
	public void testRemoveLastCharacters() {
		assertEquals(new BankRuParser().removeLastCharacters("malfunction", 3), "malfunct");
	}

	@Test
	public void testFilterLines() {
		assertEquals(new BankRuParser().filterLines("test\ntest\ntest\n"), "test test test");
	}

	@Test
	public void testFilterCommas() {
		assertEquals(new BankRuParser().filterCommas("45 000,45"), "45 000.45");
	}

	@Test
	public void testFilterSpaces() {
		assertEquals(new BankRuParser().filterSpaces("45 000,45"), "45000,45");
	}

	@Test
	public void testUpdatePrevValue() {
		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.10"),
			new BigDecimal("75.1"),
			new BigDecimal("78.4")
		).toString(), "78.4");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.1"),
			new BigDecimal("75.11"),
			new BigDecimal("78.4")
		).toString(), "75.1");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.14"),
			new BigDecimal("75.52"),
			new BigDecimal("78.4")
		).toString(), "75.14");

		assertEquals(new BankRuParser().updatePrevValue(
				new BigDecimal("75.001"),
				new BigDecimal("75.002"),
				new BigDecimal("78.4")
		).toString(), "75.001");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.00001"),
			new BigDecimal("75.00002"),
			new BigDecimal("78.4")
		).toString(), "75.00001");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.000001"),
			new BigDecimal("75.000002"),
			new BigDecimal("78.4")
		).toString(), "75.000001");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.0000001"),
			new BigDecimal("75.0000002"),
			new BigDecimal("78.4")
		).toString(), "75.0000001");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.00000001"),
			new BigDecimal("75.00000002"),
			new BigDecimal("78.4")
		).toString(), "75.00000001");

		assertEquals(new BankRuParser().updatePrevValue(
			new BigDecimal("75.000000001"),
			new BigDecimal("75.000000002"),
			new BigDecimal("78.4")
		).toString(), "78.4");
	}

	private void generalTests(GeneralParser parser) {
		assertFalse(parserHelper.process(parser, "malfunction data"));
		assertFalse(parserHelper.process(parser, "malfunction\ndata"));
		assertFalse(parserHelper.process(parser, "malfunction\t\ndata"));
		assertFalse(parserHelper.process(parser, "malfunction\t\r\ndata"));
		assertFalse(parserHelper.process(parser, "malfunction\t\t\t\t\n\r\n\r\ndata"));
		assertFalse(parserHelper.process(parser, ""));
		assertFalse(parserHelper.process(parser, null));
	}
}
