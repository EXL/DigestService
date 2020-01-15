package ru.exlmoto.digest.chart;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	public void testGetButtonLabel() {
		String res = chartService.getButtonLabel("usd_rub");
		assertThat(res).isNotEmpty();
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		assertThrows(NullPointerException.class, () -> chartService.getButtonLabel("unknown_key"));
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
		System.out.println(chart.getApiUrl());
	}
}
