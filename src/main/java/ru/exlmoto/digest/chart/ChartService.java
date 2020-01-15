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
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChartService {
	@Value("classpath:chart/charts.yaml")
	private Resource yamlFile;

	private final ResourceHelper resourceHelper;

	private Map<String, ChartGeneral> yamlMap;

	@PostConstruct
	private void setUp() {
		yamlMap = parseChartsYamlFile(resourceHelper.asString(yamlFile));
	}

	public Map<String, ChartGeneral> parseChartsYamlFile(String yaml) {
		Map<String, Map<String, String>> parsedYaml = new Yaml().load(yaml);
		Map<String, ChartGeneral> res = new HashMap<>();
		parsedYaml.forEach((k, v) -> res.put(k, new ChartGeneral(v)));
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
		yamlMap.forEach((k, v) -> keys.add(k));
		return keys;
	}
}
