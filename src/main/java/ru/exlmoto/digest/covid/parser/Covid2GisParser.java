package ru.exlmoto.digest.covid.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.covid.json.Region;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Comparator;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class Covid2GisParser {
	private final Logger log = LoggerFactory.getLogger(Covid2GisParser.class);

	private final RestHelper rest;
	private final FilterHelper filter;
	private final LocaleHelper locale;

	private final String NAMES_URL = "/public/21.js";
	private final String CASES_URL = "/public/22.js";
	private final String HISTS_URL = "/public/23.js";

	private final String NAMES_MARKER = "var t=";
	private final String CASES_MARKER = "r=";
	private final String HISTS_MARKER = "var r=";
	private final String END_MARKER = "}}]);";

	public Covid2GisParser(RestHelper rest, FilterHelper filter, LocaleHelper locale) {
		this.rest = rest;
		this.filter = filter;
		this.locale = locale;
	}

	public Answer<Pair<List<Region>, Map<String, String>>> parse2GisData(String covidUrl) {
		String url = filter.checkLink(covidUrl);
		Map<String, String> names;
		Map<String, String> hists;
		List<Region> cases;

		Answer<String> resNames = rest.getRestResponse(url + NAMES_URL);
		if (resNames.ok()) {
			names = getNameMap(resNames.answer());
			if (names == null) {
				return Error(locale.i18n("covid.error.names"));
			}
		} else {
			return Error(resNames.error());
		}

		Answer<String> resCases = rest.getRestResponse(url + CASES_URL);
		if (resCases.ok()) {
			cases = getCaseMap(resCases.answer(), names);
			if (cases == null) {
				return Error(locale.i18n("covid.error.cases"));
			}
		} else {
			return Error(resCases.error());
		}

		Answer<String> resHists = rest.getRestResponse(url + HISTS_URL);
		if (resHists.ok()) {
			hists = getHistMap(resHists.answer());
			if (hists == null) {
				return Error(locale.i18n("covid.error.hists"));
			}
		} else {
			return Error(resHists.error());
		}

		return Ok(Pair.of(cases, hists));
	}

	private String chopData(String data, String start, String end) {
		if (data.contains(start) && data.contains(end)) {
			return data.substring(data.indexOf(start) + start.length(), data.indexOf(end));
		}
		return null;
	}

	private Map<String, String> getNameMap(String data) {
		String rawJson = chopData(data, NAMES_MARKER, END_MARKER);
		if (rawJson != null) {
			Map<String, String> names = new HashMap<>();

			JsonObject object = JsonParser.parseString(rawJson).getAsJsonObject();
			object.keySet().forEach(key ->
				names.put(key,
					new String(object.getAsJsonObject(key).getAsJsonPrimitive("name").getAsString()
						.getBytes(StandardCharsets.ISO_8859_1))));

			return names;
		}
		return null;
	}

	private List<Region> getCaseMap(String data, Map<String, String> names) {
		String rawJson = chopData(data, CASES_MARKER, END_MARKER);
		if (rawJson != null) {
			List<Region> cases = new ArrayList<>();

			JsonObject object = JsonParser.parseString(rawJson).getAsJsonObject();
			object.keySet().forEach(key -> {
				if (!key.equals("0")) {
					JsonObject inner = object.getAsJsonObject(key);
					cases.add(
						new Region(
							names.get(key),
							inner.getAsJsonPrimitive("cases").getAsLong(),
							inner.getAsJsonPrimitive("recover").getAsLong(),
							inner.getAsJsonPrimitive("deaths").getAsLong(),
							inner.getAsJsonPrimitive("diff").getAsLong(),
							inner.getAsJsonPrimitive("sick").getAsLong()
						)
					);
				}
			});

			cases.sort(Comparator.comparingLong(Region::getCases));
			Collections.reverse(cases);
			return cases;
		}
		return null;
	}

	private Map<String, String> getHistMap(String data) {
		String rawJson = chopData(data, HISTS_MARKER, END_MARKER);
		if (rawJson != null) {
			Map<String, String> history = new HashMap<>();

			JsonArray array = JsonParser.parseString(rawJson).getAsJsonArray();
			JsonObject previous = array.get(array.size() - 2).getAsJsonObject();
			JsonObject last = array.get(array.size() - 1).getAsJsonObject();

			String last_cases = last.getAsJsonPrimitive("cases").getAsString();
			String prev_cases = previous.getAsJsonPrimitive("cases").getAsString();
			String last_deaths = last.getAsJsonPrimitive("deaths").getAsString();
			String prev_deaths = previous.getAsJsonPrimitive("deaths").getAsString();
			String last_recover = last.getAsJsonPrimitive("recover").getAsString();
			String prev_recover = previous.getAsJsonPrimitive("recover").getAsString();

			history.put("date", last.getAsJsonPrimitive("date").getAsString());
			history.put("cases", last_cases);
			history.put("cases_diff", getDifference(prev_cases, last_cases));
			history.put("deaths", last_deaths);
			history.put("deaths_diff", getDifference(prev_deaths, last_deaths));
			history.put("recover", last_recover);
			history.put("recover_diff", getDifference(prev_recover, last_recover));

			return history;
		}
		return null;
	}

	private String getDifference(String previous, String last) {
		try {
			long prev = Long.parseLong(previous);
			long curr = Long.parseLong(last);
			return String.valueOf(curr - prev);
		} catch (NumberFormatException nfe) {
			log.error("Cannot cast 'previous' or 'last' value from String to Long.", nfe);
		}
		return "0";
	}
}
