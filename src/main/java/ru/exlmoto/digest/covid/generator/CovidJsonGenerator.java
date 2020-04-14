package ru.exlmoto.digest.covid.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.covid.json.Region;
import ru.exlmoto.digest.covid.parser.Covid2GisParser;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class CovidJsonGenerator {
	private final Covid2GisParser parser;

	private Gson gson;

	public CovidJsonGenerator(Covid2GisParser parser) {
		this.parser = parser;
	}

	@PostConstruct
	private void setUp() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public String getJsonReport(String covidUrl) {
		Answer<Pair<List<Region>, Map<String, String>>> res = parser.parse2GisData(covidUrl);
		if (res.ok()) {
			Pair<List<Region>, Map<String, String>> answer = res.answer();
			return generateJsonReport(answer.getFirst(), answer.getSecond());
		} else {
			JsonObject error = new JsonObject();
			error.addProperty("error", res.error());
			return error.toString();
		}
	}

	private String generateJsonReport(List<Region> cases, Map<String, String> history) {
		JsonObject report = new JsonObject();
		report.add("history", getHistory(history));
		report.add("data", getCaseReport(cases));
		return gson.toJson(report);
	}

	private JsonObject getHistory(Map<String, String> history) {
		return JsonParser.parseString(gson.toJson(history)).getAsJsonObject();
	}

	private JsonArray getCaseReport(List<Region> cases) {
		JsonArray array = new JsonArray();
		cases.forEach(value -> array.add(JsonParser.parseString(gson.toJson(value)).getAsJsonObject()));
		return array;
	}
}
