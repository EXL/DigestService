package ru.exlmoto.digest.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.yaml.snakeyaml.Yaml;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.file.ResourceHelper;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Service
public class ChartService {
	private final Logger log = LoggerFactory.getLogger(ChartService.class);

	@Value("${general.lang}")
	private String langTag;

	@Value("classpath:chart/charts.yaml")
	private Resource yamlFile;

	private final ResourceHelper resourceHelper;
	private final ImageHelper imageHelper;

	private Map<String, Chart> chartMap;

	public ChartService(ResourceHelper resourceHelper, ImageHelper imageHelper) {
		this.resourceHelper = resourceHelper;
		this.imageHelper = imageHelper;
	}

	@PostConstruct
	private void setUp() {
		chartMap = parseChartsYamlFile(resourceHelper.asString(yamlFile), langTag);
	}

	public Map<String, Chart> parseChartsYamlFile(String yaml, String lang) {
		Map<String, Map<String, String>> yamlMap = new Yaml().load(yaml);
		Map<String, Chart> res = new LinkedHashMap<>();
		yamlMap.forEach((k, v) -> res.put(k, new Chart(v, lang)));
		StringJoiner joiner = new StringJoiner(", ");
		res.forEach((k, v) -> {
			Assert.isTrue(v.isValid(), "Cannot check Chart object. Is YAML file valid?");
			joiner.add(k);
		});
		log.info(String.format("Loaded chart keys: [%s]", joiner));
		return res;
	}

	public List<String> getChartKeys() {
		List<String> keys = new ArrayList<>();
		chartMap.forEach((k, v) -> keys.add(k));
		return keys;
	}

	public String getButtonLabel(String key) {
		return chartMap.get(key).getButton();
	}

	public Answer<Chart> getChart(String key) {
		if (!getChartKeys().contains(key)) {
			return Error(String.format("Unknown key '%s' for charts!", key));
		}
		Chart chart = chartMap.get(key);
		Answer<String> res = imageHelper.getImageByLink(chart.getUrl());
		if (res.ok()) {
			chart.setPath(res.answer());
			return Ok(chart);
		}
		return Error(res.error());
	}
}
