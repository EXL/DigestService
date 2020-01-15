package ru.exlmoto.digest.chart;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import ru.exlmoto.digest.util.resource.ResourceHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartServiceTest {
	@Value("classpath:chart/charts.yaml")
	private Resource yamlFile;

	@Autowired
	private ChartService chartService;

	@Autowired
	private ResourceHelper resourceHelper;

	@Test
	public void testParseChartsYamlFile() {
		// System.out.println(chartService.parseChartsYamlFile(resourceHelper.asString(yamlFile)));
		ChartService chartService = new ChartService(resourceHelper);
		assertThat(chartService.parseChartsYamlFile(resourceHelper.asString(yamlFile))).isNotEmpty();
	}

	@Test
	public void testGetChartKeys() {
		List<String> keys = chartService.getChartKeys();
		assertThat(keys).isNotEmpty();
		System.out.println(keys);
	}
}
