/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

	public String tgHtmlRuReport() {
		return htmlGenerator.getTgHtmlReport(covidUrl, CASES_RU_PATH, HISTORY_RU_PATH);
	}

	public String tgHtmlUaReport() {
		return htmlGenerator.getTgHtmlReport(covidUrl, CASES_UA_PATH, HISTORY_UA_PATH);
	}
}
