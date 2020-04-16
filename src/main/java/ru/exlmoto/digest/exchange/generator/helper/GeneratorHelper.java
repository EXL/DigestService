package ru.exlmoto.digest.exchange.generator.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GeneratorHelper {
	private final Logger log = LoggerFactory.getLogger(GeneratorHelper.class);

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
		BigDecimal difference = new BigDecimal(String.format("%.2f", current.subtract(prev)));
		if (difference.signum() == 0) {
			return null;
		}
		return difference.toString();
	}

	public String getValue(String diff) {
		if (diff != null && !diff.isEmpty()) {
			try {
				new BigDecimal(diff);
				return diff;
			} catch (NumberFormatException nfe) {
				log.error(String.format("Cannot parse number value from '%s' string.", diff), nfe);
			}
		}
		return "0.0";
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
		StringBuilder builder = new StringBuilder(value);
		int start = value.length();
		for (int i = start; i < limit; i++) {
			builder.append(sign);
		}
		return builder.toString();
	}

	public boolean isDateNotEmpty(String date) {
		return date != null && !date.isEmpty() && !date.equals("null");
	}
}
