package ru.exlmoto.exchange.generator.helper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class GeneratorHelper {
	@Value("${general.lang}")
	private String langTag;

	private final MessageSource messageSource;

	public String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}

	public BigDecimal getDifference(BigDecimal prev, BigDecimal current) {
		if (prev == null || current == null) {
			return null;
		}
		if ((prev.compareTo(BigDecimal.ZERO) == 0) || (current.compareTo(BigDecimal.ZERO) == 0)) {
			return null;
		}
		if (prev.compareTo(current) == 0) {
			return null;
		}
		return prev.subtract(current);
	}

	public boolean isDifferencePositive(BigDecimal difference) {
		return difference.signum() == 1;
	}

	public String normalizeDifference(BigDecimal difference) {
		return String.format("%.2f", difference);
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
