package ru.exlmoto.motofan.generator.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.Locale;

@Component("generatorHelperMotofan")
public class GeneratorHelper {
	@Value("${general.lang}")
	private String langTag;

	private ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

	@PostConstruct
	private void setUp() {
		messageSource.setBasenames("classpath:/motofan-crawler/messages");
		messageSource.setDefaultEncoding("UTF-8");
	}

	public String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}
}
