package ru.exlmoto.exchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LanguageService {
	private final MessageSource messageSource;

	@Value("${general.lang}")
	private String langTag;

	public String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}
}
