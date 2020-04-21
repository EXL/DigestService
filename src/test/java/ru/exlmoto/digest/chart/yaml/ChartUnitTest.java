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

package ru.exlmoto.digest.chart.yaml;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartUnitTest {
	@Test
	public void testIsValid() {
		Chart chart = new Chart();
		assertFalse(chart.isValid());

		chart.setTitle("Test Value");
		assertFalse(chart.isValid());

		chart.setDesc("Test Value");
		chart.setButton("Test Value");
		assertFalse(chart.isValid());

		chart.setUrl("Test Value");
		assertTrue(chart.isValid());
	}

	@Test
	public void testConstructor() {
		Map<String, String> values = new HashMap<>();
		values.put("title_ru", "some value");
		values.put("title_en", "some value");
		values.put("desc_ru", "some value");
		values.put("desc_en", "some value");
		values.put("button_ru", "some value");
		values.put("button_en", "some value");
		Chart chartFirst = new Chart(values, "en");
		assertFalse(chartFirst.isValid());

		values.put("api_url", "some value");
		Chart chartSecond = new Chart(values, "en");
		assertTrue(chartSecond.isValid());
		assertThat(chartSecond.getPath()).isEmpty();

		Chart chartThird = new Chart(values, "fr");
		assertFalse(chartThird.isValid());
	}
}
