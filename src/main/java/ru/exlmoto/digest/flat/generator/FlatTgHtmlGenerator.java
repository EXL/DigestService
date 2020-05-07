package ru.exlmoto.digest.flat.generator;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.flat.manager.FlatManager;
import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.flat.parser.impl.FlatN1Parser;
import ru.exlmoto.digest.util.Answer;

@Component
public class FlatTgHtmlGenerator {
	private final FlatManager manager;
	private final FlatCianParser cianParser;
	private final FlatN1Parser n1Parser;

	public FlatTgHtmlGenerator(FlatManager manager, FlatCianParser cianParser, FlatN1Parser n1Parser) {
		this.manager = manager;
		this.cianParser = cianParser;
		this.n1Parser = n1Parser;
	}

	public String getTgHtmlReportCian() {
		Answer<String> flatAnswer = manager.getXlsxCianFile();
		if (flatAnswer.ok()) {
			cianParser.getAvailableFlats(flatAnswer.answer());
		}
		return flatAnswer.error();
	}

	public String getTgHtmlReportN1() {
		Answer<String> flatAnswer = manager.getJsonN1Response();
		if (flatAnswer.ok()) {
			n1Parser.getAvailableFlats(flatAnswer.answer());
		}
		return flatAnswer.error();
	}
}
