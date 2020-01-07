package ru.exlmoto.exchange.parser.helper;

import ru.exlmoto.exchange.parser.RateParser;

public class RateParserHelper {
	public boolean test(RateParser parser, String content) {
		boolean result = parser.parse(content);
		parser.logParsedValues();
		return result;
	}
}
