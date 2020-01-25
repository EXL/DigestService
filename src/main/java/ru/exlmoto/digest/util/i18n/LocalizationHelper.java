package ru.exlmoto.digest.util.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class LocalizationHelper {
	private final Logger log = LoggerFactory.getLogger(LocalizationHelper.class);

	@Value("${general.lang}")
	private String lang;

	@Value("${general.username-tag}")
	private String usernameTag;

	private final MessageSource messageSource;

	public LocalizationHelper(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String i18n(String key) {
		try {
			return messageSource.getMessage(key, null, Locale.forLanguageTag(lang));
		} catch (NoSuchMessageException nsme) {
			log.error(String.format("Message with key '%s' is missing.", key), nsme);
			return nsme.getLocalizedMessage();
		}
	}

	public String i18nU(String key, String username) {
		return i18n(key).replaceAll(usernameTag, username);
	}

	public String i18nR(String key) {
		String[] strings = i18n(key).split("\n");
		return strings[ThreadLocalRandom.current().nextInt(0, strings.length)];
	}

	public String i18nRU(String key, String username) {
		return i18nR(key).replaceAll(usernameTag, username);
	}
}
