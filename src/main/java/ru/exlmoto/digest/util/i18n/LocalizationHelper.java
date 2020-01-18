package ru.exlmoto.digest.util.i18n;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Component
public class LocalizationHelper {
	@Value("${general.lang}")
	private String lang;

	@Value("${general.username-tag}")
	private String usernameTag;

	private final MessageSource messageSource;

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
