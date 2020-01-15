package ru.exlmoto.digest.i18n;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Component
public class LocalizationHelper {
	@Value("${general.lang}")
	private String lang;

	private final MessageSource messageSource;

	public String i18n(String key) {
		try {
			return messageSource.getMessage(key, null, Locale.forLanguageTag(lang));
		} catch (NoSuchMessageException nsme) {
			log.error(String.format("Message with key '%s' is missing.", key), nsme);
			return nsme.getLocalizedMessage();
		}
	}
}
