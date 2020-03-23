package ru.exlmoto.digest.site.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.util.system.SystemReport;

@RestController
public class ApiController {
	private final SystemReport report;
	private final ExchangeService service;

	public ApiController(SystemReport report, ExchangeService service) {
		this.report = report;
		this.service = service;
	}

	@GetMapping("/api/info")
	public String info() {
		return report.getSystemReportHtml();
	}

	@GetMapping("/api/rate")
	public String rate() {
		return service.jsonReport();
	}
}