/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

import org.junit.jupiter.api.Test;

import org.yaml.snakeyaml.scanner.ScannerException;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.file.ResourceHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChartServiceUnitTest {
	private final ChartService chart = new ChartService(null, null);

	private final ResourceHelper resource = new ResourceHelper();
	
	@Test
	public void testParseChartsYamlFileIncorrect() {
		final String yamlChunk = resource.readFileToString("classpath:chart/charts-chunk.yaml");
		assertThrows(ScannerException.class, () -> chart.parseChartsYamlFile(yamlChunk, "en"));

		final String yamlError = resource.readFileToString("classpath:chart/charts-error.yaml");
		assertThrows(IllegalArgumentException.class, () -> chart.parseChartsYamlFile(yamlError, "en"));

		final String yamlWrong = resource.readFileToString("classpath:chart/charts-wrong.yaml");
		assertThrows(NullPointerException.class, () -> chart.parseChartsYamlFile(yamlWrong, "en"));

		final String yamlOther = resource.readFileToString("classpath:chart/charts-other.yaml");
		assertThrows(ClassCastException.class, () -> chart.parseChartsYamlFile(yamlOther, "en"));

		final String yamlBig = resource.readFileToString("classpath:chart/charts-big.yaml");
		Chart chartFirst = chart.parseChartsYamlFile(yamlBig, "en").get("elem1");
		Chart chartSecond = chart.parseChartsYamlFile(yamlBig, "en").get("elem2");
		assertNotNull(chartFirst);
		assertNotNull(chartSecond);
		System.out.println(chartFirst);
		System.out.println(chartSecond);

		final String yamlEmpty = resource.readFileToString("classpath:chart/charts-empty.yaml");
		assertThrows(IllegalArgumentException.class, () -> chart.parseChartsYamlFile(yamlEmpty, "en"));
	}

	@Test
	public void testParseChartsYamlFile() {
		String yamlContent = resource.readFileToString("classpath:chart/charts.yaml");

		assertThat(chart.parseChartsYamlFile(yamlContent, "en")).isNotEmpty();
		assertThat(chart.parseChartsYamlFile(yamlContent, "ru")).isNotEmpty();
		assertThrows(IllegalArgumentException.class, () -> chart.parseChartsYamlFile(yamlContent, "fr"));
	}
}
