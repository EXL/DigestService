package ru.exlmoto.digest.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	private String token;
	private String[] admins;
	private int maxUpdates;
	private int maxSendLength;
	private int cooldown;
	private boolean logUpdates;
	private boolean disableNotifications;
	private boolean silent;
	private boolean useStack;
	private boolean showGreetings;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public boolean isLogUpdates() {
		return logUpdates;
	}

	public void setLogUpdates(boolean logUpdates) {
		this.logUpdates = logUpdates;
	}

	public boolean isDisableNotifications() {
		return disableNotifications;
	}

	public void setDisableNotifications(boolean disableNotifications) {
		this.disableNotifications = disableNotifications;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isUseStack() {
		return useStack;
	}

	public void setUseStack(boolean useStack) {
		this.useStack = useStack;
	}

	public boolean isShowGreetings() {
		return showGreetings;
	}

	public void setShowGreetings(boolean showGreetings) {
		this.showGreetings = showGreetings;
	}
}
