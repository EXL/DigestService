package ru.exlmoto.exchange.generator.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.math.BigDecimal;
import java.util.Locale;

@Component("generatorHelperExchange")
public class GeneratorHelper {
	@Value("${general.lang}")
	private String langTag;

	private ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

	@PostConstruct
	private void setUp() {
		messageSource.setBasenames("classpath:/exchange-manager/messages");
		messageSource.setDefaultEncoding("UTF-8");
	}

	public String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}

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
