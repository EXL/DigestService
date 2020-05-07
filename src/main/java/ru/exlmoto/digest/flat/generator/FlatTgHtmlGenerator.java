package ru.exlmoto.digest.flat.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.parser.impl.CianParser;
import ru.exlmoto.digest.flat.parser.impl.N1Parser;

@Component
public class FlatTgHtmlGenerator {
	private final CianParser cianParser;
	private final N1Parser n1Parser;

	public FlatTgHtmlGenerator(CianParser cianParser, N1Parser n1Parser) {
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
	}

	public String getTgHtmlReportCian(String url) {
		cianParser.getAvailableFlats(url);

		return null;
	}

	public String getTgHtmlReportN1(String url) {
		n1Parser.getAvailableFlats(url);

		return null;
	}
}
