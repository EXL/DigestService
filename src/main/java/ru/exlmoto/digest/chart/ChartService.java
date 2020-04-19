/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
		Assert.notEmpty(yamlMap, "Cannot create Chart object. Is YAML file empty?");
		Map<String, Chart> res = new LinkedHashMap<>();
		yamlMap.forEach((k, v) -> res.put(k, new Chart(v, lang)));
		StringJoiner joiner = new StringJoiner(", ");
		res.forEach((k, v) -> {
			Assert.isTrue(v.isValid(), "Cannot check Chart object. Is YAML file valid?");
			joiner.add(k);
		});
		log.info(String.format("Loaded %d chart keys: [%s]", res.size(), joiner));
		return res;
	}

	public String markdownDescriptions() {
		StringBuilder builder = new StringBuilder("```\n");
		chartMap.forEach((k, v) -> builder.append(v.getDesc()).append("\n"));
		return builder.append("```").toString();
	}

	public List<String> getChartKeys() {
		List<String> keys = new ArrayList<>();
		chartMap.forEach((k, v) -> keys.add(k));
		return keys;
	}

	public String getButtonLabel(String key) {
		return chartMap.containsKey(key) ? chartMap.get(key).getButton() : "???";
	}

	public String getTitle(String key) {
		return chartMap.containsKey(key) ? chartMap.get(key).getTitle() : "???";
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
