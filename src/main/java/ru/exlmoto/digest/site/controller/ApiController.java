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

	@GetMapping("/api/info")
	public String info() {
		return report.getSystemReportHtml();
	}

	@GetMapping("/api/rate")
	public String rate() {
		return service.jsonReport();
	}

	@GetMapping("/api/covid/ru")
	public String covidRu() {
		return covid.jsonRuReport();
	}

	@GetMapping("/api/covid/ua")
	public String covidUa() {
		return covid.jsonUaReport();
	}
}
