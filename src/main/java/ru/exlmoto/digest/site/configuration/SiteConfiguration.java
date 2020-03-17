package ru.exlmoto.digest.site.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@ConfigurationProperties(prefix = "site")
public class SiteConfiguration {
	private String address;

	private int pagePosts;
	private int pageDeep;

	private String motofanChatSlug;
	private String[] moderators;

	private String proxy;
	private boolean proxyEnabled;

	private boolean autolinkerEnabled;

	/*
	TODO:
	@Bean
	public LocaleResolver localeResolver(@Value("general.lang") String defaultLanguage) {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		sessionLocaleResolver.setDefaultLocale(Locale.forLanguageTag(defaultLanguage));
		return sessionLocaleResolver;
	}
	 */

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPagePosts() {
		return pagePosts;
	}

	public void setPagePosts(int pagePosts) {
		this.pagePosts = pagePosts;
	}

	public int getPageDeep() {
		return pageDeep;
	}

	public void setPageDeep(int pageDeep) {
		this.pageDeep = pageDeep;
	}

	public String getMotofanChatSlug() {
		return motofanChatSlug;
	}

	public void setMotofanChatSlug(String motofanChatSlug) {
		this.motofanChatSlug = motofanChatSlug;
	}

	public String[] getModerators() {
		return moderators;
	}

	public void setModerators(String[] moderators) {
		this.moderators = moderators;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public boolean isAutolinkerEnabled() {
		return autolinkerEnabled;
	}

	public void setAutolinkerEnabled(boolean autolinkerEnabled) {
		this.autolinkerEnabled = autolinkerEnabled;
	}
}
