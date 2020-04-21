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

package ru.exlmoto.digest.site.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.digest.covid.CovidService;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.util.system.SystemReport;

@RestController
public class ApiController {
	private final SystemReport report;
	private final ExchangeService service;
	private final CovidService covid;

	public ApiController(SystemReport report, ExchangeService service, CovidService covid) {
		this.report = report;
		this.service = service;
		this.covid = covid;
	}

	@GetMapping(value = "/api/info", produces = "text/plain")
	public String info() {
		return report.getSystemReportMarkdown();
	}

	@GetMapping(value = "/api/rate", produces = "application/json")
	public String rate() {
		return service.jsonReport();
	}

	@GetMapping(value = "/api/covid/ru", produces = "application/json;charset=UTF-8")
	public String covidRu() {
		return covid.jsonRuReport();
	}

	@GetMapping(value = "/api/covid/ua", produces = "application/json;charset=UTF-8")
	public String covidUa() {
		return covid.jsonUaReport();
	}
}
