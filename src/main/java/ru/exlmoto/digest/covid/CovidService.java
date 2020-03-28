package ru.exlmoto.digest.covid;

import org.springframework.stereotype.Service;

import ru.exlmoto.digest.covid.generator.CovidJsonGenerator;
import ru.exlmoto.digest.covid.generator.CovidTgHtmlGenerator;

@Service
public class CovidService {
	private final CovidJsonGenerator jsonGenerator;
	private final CovidTgHtmlGenerator htmlGenerator;

	public CovidService(CovidJsonGenerator jsonGenerator, CovidTgHtmlGenerator htmlGenerator) {
		this.jsonGenerator = jsonGenerator;
		this.htmlGenerator = htmlGenerator;
	}

	public String jsonReport() {
		return jsonGenerator.getJsonReport();
	}

	public String tgHtmlReport() {
		return htmlGenerator.getTgHtmlReport();
	}
}
