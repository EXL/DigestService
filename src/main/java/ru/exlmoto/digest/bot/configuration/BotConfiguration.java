package ru.exlmoto.digest.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.telegram.telegrambots.ApiContextInitializer;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	private String token;
	private String username;
	private String[] admins;
	private int maxUpdates;
	private int maxSendLength;
	private int callbackCooldown;
	private boolean enableNotifications;
	private boolean logUpdates;
	private boolean showGreetings;
	private boolean silent;
	private boolean logSends;
	private boolean useStack;

	@PostConstruct
	private void telegramBotApiInitialization() {
		ApiContextInitializer.init();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getAdmins() {
		return admins;
	}

	public void setAdmins(String[] admins) {
		this.admins = admins;
	}

	public int getMaxUpdates() {
		return maxUpdates;
	}

	public void setMaxUpdates(int maxUpdates) {
		this.maxUpdates = maxUpdates;
	}

	public int getMaxSendLength() {
		return maxSendLength;
	}

	public void setMaxSendLength(int maxSendLength) {
		this.maxSendLength = maxSendLength;
	}

	public int getCallbackCooldown() {
		return callbackCooldown;
	}

	public void setCallbackCooldown(int callbackCooldown) {
		this.callbackCooldown = callbackCooldown;
	}

	public boolean isEnableNotifications() {
		return enableNotifications;
	}

	public void setEnableNotifications(boolean enableNotifications) {
		this.enableNotifications = enableNotifications;
	}

	public boolean isLogUpdates() {
		return logUpdates;
	}

	public void setLogUpdates(boolean logUpdates) {
		this.logUpdates = logUpdates;
	}

	public boolean isShowGreetings() {
		return showGreetings;
	}

	public void setShowGreetings(boolean showGreetings) {
		this.showGreetings = showGreetings;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isLogSends() {
		return logSends;
	}

	public void setLogSends(boolean logSends) {
		this.logSends = logSends;
	}

	public boolean isUseStack() {
		return useStack;
	}

	public void setUseStack(boolean useStack) {
		this.useStack = useStack;
	}
}
