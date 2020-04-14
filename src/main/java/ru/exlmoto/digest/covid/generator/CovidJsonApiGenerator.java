package ru.exlmoto.digest.covid.generator;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import ru.exlmoto.digest.covid.json.Region;
import ru.exlmoto.digest.covid.json.RegionFull;
import ru.exlmoto.digest.covid.parser.Covid2GisApiParser;
import ru.exlmoto.digest.util.Answer;

import java.util.List;
import java.util.Map;

@Component
public class CovidJsonApiGenerator {
	private final Covid2GisApiParser parser;

	public CovidJsonApiGenerator(Covid2GisApiParser parser) {
		this.parser = parser;
	}

	public String getJsonReport(String covidUrl, String casesPath, String historyPath) {
		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, casesPath, historyPath);

		return null;
	}
}
