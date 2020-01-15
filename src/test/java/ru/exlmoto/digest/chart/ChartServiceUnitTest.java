package ru.exlmoto.digest.chart;

import org.junit.jupiter.api.Test;

import org.yaml.snakeyaml.scanner.ScannerException;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.resource.ResourceHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChartServiceUnitTest {
	@Test
	public void testParseChartsYamlFileIncorrect() {
		ChartService chartService = new ChartService(null, null);
		ResourceHelper resourceHelper = new ResourceHelper();

		final String yamlChunk = resourceHelper.readFileToString("classpath:chart/charts-chunk.yaml");
		assertThrows(ScannerException.class, () -> chartService.parseChartsYamlFile(yamlChunk, "en"));

		final String yamlError = resourceHelper.readFileToString("classpath:chart/charts-error.yaml");
		assertThrows(IllegalArgumentException.class, () -> chartService.parseChartsYamlFile(yamlError, "en"));

		final String yamlWrong = resourceHelper.readFileToString("classpath:chart/charts-wrong.yaml");
		assertThrows(NullPointerException.class, () -> chartService.parseChartsYamlFile(yamlWrong, "en"));

		final String yamlOther = resourceHelper.readFileToString("classpath:chart/charts-other.yaml");
		assertThrows(ClassCastException.class, () -> chartService.parseChartsYamlFile(yamlOther, "en"));

		final String yamlBig = resourceHelper.readFileToString("classpath:chart/charts-big.yaml");
		Chart chartFirst = chartService.parseChartsYamlFile(yamlBig, "en").get("elem1");
		Chart chartSecond = chartService.parseChartsYamlFile(yamlBig, "en").get("elem2");
		assertNotNull(chartFirst);
		assertNotNull(chartSecond);
		System.out.println(chartFirst);
		System.out.println(chartSecond);
	}

	@Test
	public void testParseChartsYamlFile() {
		ChartService chartService = new ChartService(null, null);
		String yamlContent = new ResourceHelper().readFileToString("classpath:chart/charts.yaml");

		assertThat(chartService.parseChartsYamlFile(yamlContent, "en")).isNotEmpty();
		assertThat(chartService.parseChartsYamlFile(yamlContent, "ru")).isNotEmpty();
		assertThrows(IllegalArgumentException.class,
			() -> chartService.parseChartsYamlFile(yamlContent, "fr"));
	}
}
