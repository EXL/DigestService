package ru.exlmoto.digest.covid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.covid.generator.CovidJsonGenerator;
import ru.exlmoto.digest.covid.generator.CovidTgHtmlGenerator;

@Service
public class CovidService {
	private final CovidJsonGenerator jsonGenerator;
	private final CovidTgHtmlGenerator htmlGenerator;

	@Value("${covid.url}")
	private String covidUrl;

	private final String CASES_RU_PATH = "covid19-ru-by-territory.json";
	private final String HISTORY_RU_PATH = "covid19-ru-history.json";
	private final String CASES_UA_PATH = "covid19-ua-by-territory.json";
	private final String HISTORY_UA_PATH = "covid19-ua-history.json";

	public CovidService(CovidJsonGenerator jsonGenerator, CovidTgHtmlGenerator htmlGenerator) {
		this.jsonGenerator = jsonGenerator;
		this.htmlGenerator = htmlGenerator;
	}

	public String jsonRuReport() {
		return jsonGenerator.getJsonReport(covidUrl, CASES_RU_PATH, HISTORY_RU_PATH);
	}

	public String jsonUaReport() {
		return jsonGenerator.getJsonReport(covidUrl, CASES_UA_PATH, HISTORY_UA_PATH);
	}

	public String tgHtmlReport() {
		return htmlGenerator.getTgHtmlReport(covidUrl);
	}
}
