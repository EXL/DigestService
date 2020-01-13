package ru.exlmoto.chart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.exlmoto.chart.ChartConfiguration;

import java.util.List;

@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(ChartConfiguration.class)
public class ChartService {
	private final ChartConfiguration config;

	//public Chart getChart(String key) {
	//	return null;
	//}

	public List<String> getChartList() {
		return null;
	}

	public String getChartHelp() {
		return null;
	}
}
