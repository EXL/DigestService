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
