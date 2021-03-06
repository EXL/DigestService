/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.covid.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class Covid2GisApiParser {
	private final Logger log = LoggerFactory.getLogger(Covid2GisApiParser.class);

	private final RestHelper rest;
	private final Covid2GisParser parser;
	private final FilterHelper filter;
	private final LocaleHelper locale;

	public Covid2GisApiParser(RestHelper rest, Covid2GisParser parser, FilterHelper filter, LocaleHelper locale) {
		this.rest = rest;
		this.parser = parser;
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
			hists = parser.parseHistsJson(resHists.answer());
			if (hists == null) {
				return Error(locale.i18n("covid.error.hists"));
			}
		} else {
			return Error(resHists.error());
		}

		return Ok(Pair.of(cases, hists));
	}

	private List<RegionFull> getCaseList(String json) {
		if (StringUtils.hasText(json)) {
			try {
				List<RegionFull> cases = new ArrayList<>();

				JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("items").forEach(item -> {
					JsonObject inner = item.getAsJsonObject();
					cases.add(new RegionFull(
						inner.getAsJsonPrimitive("territoryName").getAsString(),
						inner.getAsJsonPrimitive("confirmed").getAsLong(),
						inner.getAsJsonPrimitive("recovered").getAsLong(),
						inner.getAsJsonPrimitive("deaths").getAsLong(),
						inner.getAsJsonPrimitive("confirmedInc").getAsLong(),
						inner.getAsJsonPrimitive("recoveredInc").getAsLong(),
						inner.getAsJsonPrimitive("deathsInc").getAsLong()
					));
				});

				if (!cases.isEmpty()) {
					cases.sort(Comparator.comparingLong(RegionFull::getConfirmed));
					Collections.reverse(cases);
					return cases;
				}
			} catch (JsonSyntaxException jse) {
				log.error("Cannot parse JSON cases data!", jse);
			}
		}
		return null;
	}
}
