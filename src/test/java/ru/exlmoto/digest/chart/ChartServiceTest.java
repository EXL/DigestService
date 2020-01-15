package ru.exlmoto.digest.chart;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import ru.exlmoto.digest.util.resource.ResourceHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ChartServiceTest {
	@Value("classpath:chart/charts.yaml")
	private Resource yamlFile;

	@Autowired
	private ChartService chartService;

	@Test
	public void testParseChartsYamlFile() {
		ChartService chartService = new ChartService(null);
		assertThat(chartService.parseChartsYamlFile(new ResourceHelper().asString(yamlFile), "en")).isNotEmpty();
		assertThat(chartService.parseChartsYamlFile(new ResourceHelper().asString(yamlFile), "ru")).isNotEmpty();
		assertThrows(IllegalArgumentException.class,
			() -> chartService.parseChartsYamlFile(new ResourceHelper().asString(yamlFile), "fr"));
	}

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
}
