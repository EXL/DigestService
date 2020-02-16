package ru.exlmoto.digest.site.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@ConfigurationProperties(prefix = "site")
public class SiteConfiguration {
	/*
	@Bean
	public LocaleResolver localeResolver(@Value("general.lang") String defaultLanguage) {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(defaultLanguage));
		return sessionLocaleResolver;
	}
	 */
}
