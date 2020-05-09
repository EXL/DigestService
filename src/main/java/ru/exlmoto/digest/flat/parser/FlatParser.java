package ru.exlmoto.digest.flat.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StringUtils;

import ru.exlmoto.digest.flat.model.Flat;
import ru.exlmoto.digest.util.Answer;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;

public abstract class FlatParser {
	private final Logger log = LoggerFactory.getLogger(FlatParser.class);

	protected final boolean onePhone = true;

	/*
	 * For convenience, prices with millions should use the number format that is used in the United States.
	 * Example: 2,300,300 rub.
	 */
	protected String adjustPrice(String price) {
		int parsed = -1;
		try {
			parsed = Integer.parseInt(price);
		} catch (NumberFormatException nfe) {
			log.error(String.format("Cannot parse price '%s' value to Integer.", price), nfe);
		}
		if (parsed != -1) {
			return NumberFormat.getInstance(Locale.US).format(parsed);
		}
		return price;
	}

	protected String applyPhonePatch(String phone) {
		if (StringUtils.hasText(phone) && phone.startsWith("+7")) {
			return "8" + phone.substring(2);
		}
		return phone;
	}

	public abstract Answer<List<Flat>> getAvailableFlats(String content);
}
