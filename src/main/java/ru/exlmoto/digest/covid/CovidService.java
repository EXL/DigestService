package ru.exlmoto.digest.covid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.covid.generator.CovidJsonApiGenerator;

@Service
public class CovidService {
	private final CovidJsonApiGenerator jsonApiGenerator;

	@Value("${covid.url}")
	private String covidUrl;

	private final String CASES_RU_PATH = "covid19-ru-by-territory.json";
	private final String HISTORY_RU_PATH = "covid19-ru-history.json";

	public CovidService(CovidJsonApiGenerator jsonApiGenerator) {
		this.jsonApiGenerator = jsonApiGenerator;
	}

	public String jsonReportRu() {
		return jsonApiGenerator.getJsonReport(covidUrl, CASES_RU_PATH, HISTORY_RU_PATH);
	}
}
