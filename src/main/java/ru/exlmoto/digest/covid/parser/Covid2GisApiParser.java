package ru.exlmoto.digest.covid.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import org.springframework.util.StringUtils;
import ru.exlmoto.digest.covid.json.RegionFull;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class Covid2GisApiParser {
	private final Logger log = LoggerFactory.getLogger(Covid2GisApiParser.class);

	private final RestHelper rest;
	private final FilterHelper filter;
	private final LocaleHelper locale;

	public Covid2GisApiParser(RestHelper rest, FilterHelper filter, LocaleHelper locale) {
		this.rest = rest;
		this.filter = filter;
		this.locale = locale;
	}

	public Answer<Pair<List<RegionFull>, Map<String, String>>> parse2GisApiData(String covidUrl,
	                                                                            String casesPath,
	                                                                            String historyPath) {
		String url = filter.checkLink(covidUrl);
		List<RegionFull> cases;
		Map<String, String> hists;

		Answer<String> resCases = rest.getRestResponse(url + casesPath);
		if (resCases.ok()) {
			cases = getCaseList(resCases.answer());
			if (cases == null) {
				return Error(locale.i18n("covid.error.cases"));
			}
		} else {
			return Error(resCases.error());
		}

		Answer<String> resHists = rest.getRestResponse(url + historyPath);
		if (resHists.ok()) {
			hists = null;
		} else {
			return Error(resHists.error());
		}

		return Ok(Pair.of(cases, hists));
	}

	private List<RegionFull> getCaseList(String json) {
		if (!StringUtils.isEmpty(json)) {
			List<RegionFull> cases = new ArrayList<>();

			JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("items").forEach(item -> {
				System.out.println(item);
			});


		}
		return null;
	}
}
