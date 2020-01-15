package ru.exlmoto.digest.chart.yaml;

import org.junit.jupiter.api.Test;

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
}
