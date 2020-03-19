package ru.exlmoto.digest.site.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.digest.util.system.SystemReport;

@RestController
public class ApiController {
	private final SystemReport report;

	public ApiController(SystemReport report) {
		this.report = report;
	}

	@GetMapping("/api/health")
	public String health() {
		return report.getSystemReportHtml();
	}
}
