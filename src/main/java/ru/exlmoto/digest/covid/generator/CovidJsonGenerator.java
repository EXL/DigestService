/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.covid.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.covid.json.RegionFull;
import ru.exlmoto.digest.covid.parser.Covid2GisApiParser;
import ru.exlmoto.digest.util.Answer;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;

@Component
public class CovidJsonGenerator {
	private final Covid2GisApiParser parser;

	private Gson gson;

	public CovidJsonGenerator(Covid2GisApiParser parser) {
		this.parser = parser;
	}

	@PostConstruct
	private void setUp() {
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public String getJsonReport(String covidUrl, String casesPath, String historyPath) {
		Answer<Pair<List<RegionFull>, Map<String, String>>> res =
			parser.parse2GisApiData(covidUrl, casesPath, historyPath);
		if (res.ok()) {
			Pair<List<RegionFull>, Map<String, String>> answer = res.answer();
			return generateJsonReport(answer.getFirst(), answer.getSecond());
		} else {
			JsonObject error = new JsonObject();
			error.addProperty("error", res.error());
			return error.toString();
		}
	}

	private String generateJsonReport(List<RegionFull> cases, Map<String, String> history) {
		JsonObject report = new JsonObject();
		report.add("history", getHistory(history));
		report.add("data", getCaseReport(cases));
		return gson.toJson(report);
	}

	private JsonObject getHistory(Map<String, String> history) {
		return JsonParser.parseString(gson.toJson(history)).getAsJsonObject();
	}

	private JsonArray getCaseReport(List<RegionFull> cases) {
		JsonArray array = new JsonArray();
		// cases.forEach(value -> array.add(JsonParser.parseString(gson.toJson(value)).getAsJsonObject()));
		cases.forEach(value -> {
			JsonObject territory = new JsonObject();
			territory.addProperty("territoryName", value.getTerritoryName());
			territory.addProperty("confirmed", String.valueOf(value.getConfirmed()));
			territory.addProperty("recovered", String.valueOf(value.getRecovered()));
			territory.addProperty("deaths", String.valueOf(value.getDeaths()));
			territory.addProperty("confirmedInc", String.valueOf(value.getConfirmedInc()));
			territory.addProperty("recoveredInc", String.valueOf(value.getRecoveredInc()));
			territory.addProperty("deathsInc", String.valueOf(value.getDeathsInc()));
			array.add(territory);
		});
		return array;
	}
}
