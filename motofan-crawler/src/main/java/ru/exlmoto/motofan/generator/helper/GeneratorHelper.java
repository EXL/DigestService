package ru.exlmoto.motofan.generator.helper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component("generatorHelperMotofan")
public class GeneratorHelper {
	@Value("${general.lang}")
	private String langTag;

	private final MessageSource messageSource;

	public String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}
}
