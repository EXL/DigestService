package ru.exlmoto.digest.flat;

import org.springframework.stereotype.Service;

import ru.exlmoto.digest.flat.generator.FlatTgHtmlGenerator;

@Service
public class FlatService {
	private final FlatTgHtmlGenerator generator;

	// TEST DATA NOT COMMIT

	public FlatService(FlatTgHtmlGenerator generator) {
		this.generator = generator;
	}

	public String tgHtmlReportCian() {
		return generator.getTgHtmlReportCian();
	}

	public String tgHtmlReportN1() {
		return generator.getTgHtmlReportN1();
	}
}
