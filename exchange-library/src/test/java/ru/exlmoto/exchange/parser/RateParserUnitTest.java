package ru.exlmoto.exchange.parser;

import org.junit.jupiter.api.Test;

import ru.exlmoto.exchange.parser.helper.InnerFileHelper;
import ru.exlmoto.exchange.parser.helper.RateParserHelper;
import ru.exlmoto.exchange.parser.impl.BankRuParser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RateParserUnitTest {
	private final InnerFileHelper f = new InnerFileHelper();
	private final RateParserHelper p = new RateParserHelper();

	@Test
	public void testBankRuParser() {
		assertTrue(p.process(new BankRuParser(), f.content("bankRu.xml", "Windows-1251")));
		assertTrue(p.process(new BankRuParser(), f.content("bankRu.xml")));
		assertFalse(p.process(new BankRuParser(), f.content("bankRuChunk.xml", "Windows-1251")));
		assertTrue(p.process(new BankRuParser(), f.content("bankRuError.xml", "Windows-1251")));
		assertFalse(p.process(new BankRuParser(), f.content("bankRuErrorAll.xml", "Windows-1251")));

		assertFalse(p.process(new BankRuParser(), "mulfunction data"));
		// assertFalse(p.process(new BankRuParser(), "mulfunction\ndata"));
		// assertFalse(p.process(new BankRuParser(), "mulfunction\t\ndata"));
		// assertFalse(p.process(new BankRuParser(), "mulfunction\t\r\ndata"));
		// assertFalse(p.process(new BankRuParser(), "mulfunction\t\t\t\t\n\r\n\r\ndata"));
		assertFalse(p.process(new BankRuParser(), ""));
		assertFalse(p.process(new BankRuParser(), null));
	}
}
