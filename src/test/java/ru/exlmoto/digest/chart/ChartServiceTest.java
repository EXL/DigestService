package ru.exlmoto.digest.chart;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ChartServiceTest {
	@Autowired
	private ChartService chartService;

	@Test
	public void testGetChartKeys() {
		List<String> keys = chartService.getChartKeys();
		assertThat(keys).isNotEmpty();
		System.out.println(keys);
	}

	@Test
	public void testGetTitle() {
		String title = chartService.getTitle("usd_rub");
		assertThat(title).isNotEmpty();
		assertThat(title).isInstanceOf(String.class);
		System.out.println(title);

		title = chartService.getTitle("unknown_key");
		assertEquals("???", title);
	}

	@Test
	public void testMarkdownDescriptions() {
		String descriptions = chartService.markdownDescriptions();
		assertThat(descriptions).isNotEmpty();
		assertThat(descriptions).isInstanceOf(String.class);
		System.out.println(descriptions);
	}

	@Test
	public void testGetButtonLabel() {
		String label = chartService.getButtonLabel("usd_rub");
		assertThat(label).isNotEmpty();
		assertThat(label).isInstanceOf(String.class);
		System.out.println(label);

		label = chartService.getButtonLabel("unknown_key");
		assertEquals("???", label);
	}

	@Test
	public void testGetChart() {
		Answer<Chart> res = chartService.getChart("unknown_key");
		assertFalse(res.ok());
		System.out.println(res.error());

		Chart chart = chartService.getChart("usd_rub").answer();
		assertNotNull(chart);
		System.out.println(chart.getTitle());
		System.out.println(chart.getDesc());
		System.out.println(chart.getButton());
		System.out.println(chart.getUrl());
		System.out.println(chart.getPath());
	}
}
