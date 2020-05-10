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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.chart.yaml.Chart;
import ru.exlmoto.digest.util.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ChartServiceTest {
	@Autowired
	private ChartService chartService;

	@Test
	public void testGetChartKeys() {
		System.out.println("=== START testGetChartKeys() ===");
		List<String> keys = chartService.getChartKeys();
		assertThat(keys).isNotEmpty();
		System.out.println(keys);
		System.out.println("=== END testGetChartKeys() ===");
	}

	@Test
	public void testGetTitle() {
		System.out.println("=== START testGetTitle() ===");
		String title = chartService.getTitle("usd_rub");
		assertThat(title).isNotEmpty();
		assertThat(title).isInstanceOf(String.class);
		System.out.println(title);

		title = chartService.getTitle("unknown_key");
		assertEquals("???", title);
		System.out.println("=== END testGetTitle() ===");
	}

	@Test
	public void testMarkdownDescriptions() {
		System.out.println("=== START testMarkdownDescriptions() ===");
		String descriptions = chartService.markdownDescriptions();
		assertThat(descriptions).isNotEmpty();
		assertThat(descriptions).isInstanceOf(String.class);
		System.out.println(descriptions);
		System.out.println("=== END testMarkdownDescriptions() ===");
	}

	@Test
	public void testGetButtonLabel() {
		System.out.println("=== START testGetButtonLabel() ===");
		String label = chartService.getButtonLabel("usd_rub");
		assertThat(label).isNotEmpty();
		assertThat(label).isInstanceOf(String.class);
		System.out.println(label);

		label = chartService.getButtonLabel("unknown_key");
		assertEquals("???", label);
		System.out.println("=== END testGetButtonLabel() ===");
	}

	@Test
	public void testGetChart() {
		System.out.println("=== START testGetChart() ===");
		Answer<Chart> res = chartService.getChart("unknown_key");
		assertFalse(res.ok());
		System.out.println(res.error());

		Chart chart = chartService.getChart("usd_rub").answer();
		assertNotNull(chart);
		System.out.println(chart.getTitle());
		System.out.println(chart.getDesc());
		System.out.println(chart.getButton());
		System.out.println(chart.getUrl());
		System.out.println(chart.getPath());
		System.out.println("=== END testGetChart() ===");
	}
}
