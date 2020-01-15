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

		chart.setTitleEn("Test Value");
		assertFalse(chart.isValid());

		chart.setTitleRu("Test Value");
		chart.setDescRu("Test Value");
		chart.setDescEn("Test Value");
		chart.setButtonRu("Test Value");
		chart.setButtonEn("Test Value");
		assertFalse(chart.isValid());

		chart.setApiUrl("Test Value");
		assertTrue(chart.isValid());
	}

	@Test
	public void testConstructor() {
		Map<String, String> values = new HashMap<>();
		values.put("titleRu", "some value");
		values.put("titleEn", "some value");
		values.put("descRu", "some value");
		values.put("descEn", "some value");
		values.put("buttonRu", "some value");
		values.put("buttonEn", "some value");
		ChartGeneral chartFirst = new ChartGeneral(values);
		assertFalse(chartFirst.isValid());

		values.put("apiUrl", "some value");
		ChartGeneral chartSecond = new ChartGeneral(values);
		assertTrue(chartSecond.isValid());
	}
}
