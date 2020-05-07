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

	public String getTgHtmlReportCianFirst() {
		Answer<String> res = manager.getHtmlContentCianFirst();
		if (res.ok()) {
			cianParser.getAvailableFlats(res.answer());
			///
		}
		return res.error();
	}

	public String getTgHtmlReportCianSecond() {
		Answer<String> res = manager.getHtmlContentCianSecond();
		if (res.ok()) {
			cianParser.getAvailableFlats(res.answer());
			///
		}
		return res.error();
	}

	public String getTgHtmlReportN1First() {
		Answer<String> res = manager.getHtmlContentN1First();
		if (res.ok()) {
			n1Parser.getAvailableFlats(res.answer());
			///
		}
		return res.error();
	}

	public String getTgHtmlReportN1Second() {
		Answer<String> res = manager.getHtmlContentN1Second();
		if (res.ok()) {
			n1Parser.getAvailableFlats(res.answer());
			///
		}
		return res.error();
	}
}
