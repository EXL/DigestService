package ru.exlmoto.digest.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	private String token;
	private String stickerCoffee;
	private String urlHostIp;
	private String urlGame;
	private String[] admins;
	private int maxUpdates;
	private int maxSendLength;
	private int cooldown;
	private int messageDelay;
	private boolean logUpdates;
	private boolean disableNotifications;
	private boolean silent;
	private boolean useStack;
	private boolean showGreetings;
	private long motofanChatId;
	private String motofanChatUrl;
	private String telegramShortUrl;
	private int maxDigestLength;
	private int showPagePosts;
	private int digestPagePosts;
	private int digestPageCount;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStickerCoffee() {
		return stickerCoffee;
	}

	public void setStickerCoffee(String stickerCoffee) {
		this.stickerCoffee = stickerCoffee;
	}

	public String getUrlHostIp() {
		return urlHostIp;
	}

	public void setUrlHostIp(String urlHostIp) {
		this.urlHostIp = urlHostIp;
	}

	public String getUrlGame() {
		return urlGame;
	}

	public void setUrlGame(String urlGame) {
		this.urlGame = urlGame;
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

	public int getMessageDelay() {
		return messageDelay;
	}

	public void setMessageDelay(int messageDelay) {
		this.messageDelay = messageDelay;
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

	public long getMotofanChatId() {
		return motofanChatId;
	}

	public void setMotofanChatId(long motofanChatId) {
		this.motofanChatId = motofanChatId;
	}

	public String getMotofanChatUrl() {
		return motofanChatUrl;
	}

	public void setMotofanChatUrl(String motofanChatUrl) {
		this.motofanChatUrl = motofanChatUrl;
	}

	public String getTelegramShortUrl() {
		return telegramShortUrl;
	}

	public void setTelegramShortUrl(String telegramShortUrl) {
		this.telegramShortUrl = telegramShortUrl;
	}

	public int getMaxDigestLength() {
		return maxDigestLength;
	}

	public void setMaxDigestLength(int maxDigestLength) {
		this.maxDigestLength = maxDigestLength;
	}

	public int getShowPagePosts() {
		return showPagePosts;
	}

	public void setShowPagePosts(int showPagePosts) {
		this.showPagePosts = showPagePosts;
	}

	public int getDigestPagePosts() {
		return digestPagePosts;
	}

	public void setDigestPagePosts(int digestPagePosts) {
		this.digestPagePosts = digestPagePosts;
	}

	public int getDigestPageCount() {
		return digestPageCount;
	}

	public void setDigestPageCount(int digestPageCount) {
		this.digestPageCount = digestPageCount;
	}
}
