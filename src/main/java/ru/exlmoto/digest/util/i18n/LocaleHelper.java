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
public class LocaleHelper {
	private final Logger log = LoggerFactory.getLogger(LocaleHelper.class);

	@Value("${general.lang}")
	private String lang;

	@Value("${general.username-tag}")
	private String usernameTag;

	private final MessageSource messageSource;

	public LocaleHelper(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/* TODO -- TEST */
	public String i18nW(String key, Locale locale) {
		try {
			return messageSource.getMessage(key, null, locale);
		} catch (NoSuchMessageException nsme) {
			log.error(String.format("Message with key '%s' for locale '%s' is missing.", key, locale), nsme);
			return "???";
		}
	}

	public String i18n(String key) {
		return i18nW(key, Locale.forLanguageTag(lang));
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
