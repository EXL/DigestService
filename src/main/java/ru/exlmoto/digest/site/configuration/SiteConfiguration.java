package ru.exlmoto.digest.site.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@ConfigurationProperties(prefix = "site")
public class SiteConfiguration {
	private String address;

	private int pagePosts;
	private int pageDeep;
	private int pagePostsAdmin;
	private int pageDeepAdmin;

	private String motofanChatSlug;
	private String[] moderators;

	private String proxy;
	private boolean proxyEnabled;

	private boolean autolinkerEnabled;

	private boolean obeyProtection;
	private String obeyDebugUsername;
	private String obeyDebugPassword;
	private String obeyDebugRole;

	@Bean
	public LocaleResolver localeResolver() {
		final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale(Locale.forLanguageTag("ru"));
		cookieLocaleResolver.setCookieName("lang");
		return cookieLocaleResolver;
	}

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

	public int getPagePostsAdmin() {
		return pagePostsAdmin;
	}

	public void setPagePostsAdmin(int pagePostsAdmin) {
		this.pagePostsAdmin = pagePostsAdmin;
	}

	public int getPageDeepAdmin() {
		return pageDeepAdmin;
	}

	public void setPageDeepAdmin(int pageDeepAdmin) {
		this.pageDeepAdmin = pageDeepAdmin;
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

	public boolean isObeyProtection() {
		return obeyProtection;
	}

	public void setObeyProtection(boolean obeyProtection) {
		this.obeyProtection = obeyProtection;
	}

	public String getObeyDebugUsername() {
		return obeyDebugUsername;
	}

	public void setObeyDebugUsername(String obeyDebugUsername) {
		this.obeyDebugUsername = obeyDebugUsername;
	}

	public String getObeyDebugPassword() {
		return obeyDebugPassword;
	}

	public void setObeyDebugPassword(String obeyDebugPassword) {
		this.obeyDebugPassword = obeyDebugPassword;
	}

	public String getObeyDebugRole() {
		return obeyDebugRole;
	}

	public void setObeyDebugRole(String obeyDebugRole) {
		this.obeyDebugRole = obeyDebugRole;
	}
}
