package ru.exlmoto.digest.exchange.generator.helper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GeneratorHelper {
	public String getDifference(BigDecimal prev, BigDecimal current) {
		if (prev == null || current == null) {
			return null;
		}
		if ((prev.signum() == 0) || (current.signum() == 0)) {
			return null;
		}
		if (prev.compareTo(current) == 0) {
			return null;
		}
		BigDecimal difference = new BigDecimal(String.format("%.2f", prev.subtract(current)));
		if (difference.signum() == 0) {
			return null;
		}
		return difference.toString();
	}

	public String normalizeValue(BigDecimal value) {
		final int MAX_NUMBER_SIZE = 6;
		int integers = value.precision() - value.scale();
		if (integers <= 0) {
			integers = 1;
		}
		if (integers < MAX_NUMBER_SIZE) {
			return String.format("%." + (MAX_NUMBER_SIZE - integers) + "f", value);
		}
		return String.format("%.1f", value);
	}

	public String addTrailingSigns(String value, String sign, int limit) {
		StringBuilder stringBuilder = new StringBuilder(value);
		int start = value.length();
		for (int i = start; i < limit; i++) {
			stringBuilder.append(sign);
		}
		return stringBuilder.toString();
	}

	public boolean isDateNotEmpty(String date) {
		return date != null && !date.isEmpty() && !date.equals("null");
	}
}
