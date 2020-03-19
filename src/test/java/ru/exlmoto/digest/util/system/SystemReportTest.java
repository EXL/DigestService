package ru.exlmoto.digest.util.system;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SystemReportTest {
	@Autowired
	private SystemReport report;

	@Test
	public void testGetSystemReportHtml() {
		checkReport(report.getSystemReportHtml());
	}

	@Test
	public void testGetSystemReportMarkdown() {
		checkReport(report.getSystemReportMarkdown());
	}

	private void checkReport(String report) {
		assertThat(report).isNotBlank();
		System.out.println(report);
	}
}
