package ru.exlmoto.digest.chart.yaml;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartTest {
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
