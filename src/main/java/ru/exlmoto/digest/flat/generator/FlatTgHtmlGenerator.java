package ru.exlmoto.digest.flat.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.flat.parser.impl.FlatN1Parser;

import java.util.List;

@Component
public class FlatTgHtmlGenerator {
	private final FlatCianParser cianParser;
	private final FlatN1Parser n1Parser;

	public FlatTgHtmlGenerator(FlatCianParser cianParser, FlatN1Parser n1Parser) {
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
	}

	public String getTgHtmlReportCian(String url) {
		List<Flat> flatList = cianParser.getAvailableFlats(url);
		if (!flatList.isEmpty()) {

		}
		return "EMPTY";
	}

	public String getTgHtmlReportN1(String url) {
		List<Flat> flatList = n1Parser.getAvailableFlats(url);
		if (!flatList.isEmpty()) {

		}
		return "EMPTY";
	}
}
