package ru.exlmoto.digest.chart.yaml;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartGeneralTest {
	@Test
	public void testIsValid() {
		ChartGeneral chart = new ChartGeneral();
		assertFalse(chart.isValid());

		chart.setTitle("Test Value");
		assertFalse(chart.isValid());

		chart.setDesc("Test Value");
		chart.setButton("Test Value");
		assertFalse(chart.isValid());

		chart.setApiUrl("Test Value");
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
		ChartGeneral chartFirst = new ChartGeneral(values, "en");
		assertFalse(chartFirst.isValid());

		values.put("api_url", "some value");
		ChartGeneral chartSecond = new ChartGeneral(values, "en");
		assertTrue(chartSecond.isValid());

		ChartGeneral chartThird = new ChartGeneral(values, "fr");
		assertFalse(chartThird.isValid());
	}
}
