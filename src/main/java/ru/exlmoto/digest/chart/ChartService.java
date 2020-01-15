package ru.exlmoto.digest.chart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.yaml.snakeyaml.Yaml;

import ru.exlmoto.digest.chart.yaml.ChartGeneral;
import ru.exlmoto.digest.util.resource.ResourceHelper;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChartService {
	@Value("${general.lang}")
	private String lang;

	@Value("classpath:chart/charts.yaml")
	private Resource yamlFile;

	private final ResourceHelper resourceHelper;

	private Map<String, ChartGeneral> chartMap;

	@PostConstruct
	private void setUp() {
		chartMap = parseChartsYamlFile(resourceHelper.asString(yamlFile), lang);
	}

	public Map<String, ChartGeneral> parseChartsYamlFile(String yaml, String lang) {
		Map<String, Map<String, String>> yamlMap = new Yaml().load(yaml);
		Map<String, ChartGeneral> res = new HashMap<>();
		yamlMap.forEach((k, v) -> res.put(k, new ChartGeneral(v, lang)));
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
}
