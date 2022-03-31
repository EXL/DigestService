/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	private boolean initialize;
	private String token;
	private String stickerCoffee;
	private boolean urlGameImage;
	private boolean urlGameUseOwnName;
	private String urlGame;
	private String urlGameQuake2;
	private String urlGameQuake3;
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
	private boolean sendMotofanBirhdays;
	private boolean useButtonCaptcha;
	private long motofanChatId;
	private String motofanChatUrl;
	private String telegramShortUrl;
	private int maxDigestLength;
	private int showPagePosts;
	private int digestPagePosts;
	private int digestPageDeep;
	private long obsoleteDataDelay;
	private boolean digestShredder;
	private int captchaDelay;
	private int captchaBan;

	public boolean isInitialize() {
		return initialize;
	}

	public void setInitialize(boolean initialize) {
		this.initialize = initialize;
	}

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

	public boolean isUrlGameImage() {
		return urlGameImage;
	}

	public void setUrlGameImage(boolean urlGameImage) {
		this.urlGameImage = urlGameImage;
	}

	public boolean isUrlGameUseOwnName() {
		return urlGameUseOwnName;
	}

	public void setUrlGameUseOwnName(boolean urlGameUseOwnName) {
		this.urlGameUseOwnName = urlGameUseOwnName;
	}

	public String getUrlGame() {
		return urlGame;
	}

	public void setUrlGame(String urlGame) {
		this.urlGame = urlGame;
	}

	public String getUrlGameQuake2() {
		return urlGameQuake2;
	}

	public void setUrlGameQuake2(String urlGameQuake2) {
		this.urlGameQuake2 = urlGameQuake2;
	}

	public String getUrlGameQuake3() {
		return urlGameQuake3;
	}

	public void setUrlGameQuake3(String urlGameQuake3) {
		this.urlGameQuake3 = urlGameQuake3;
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

	public boolean isSendMotofanBirthdays() {
		return sendMotofanBirhdays;
	}

	public void setSendMotofanBirthdays(boolean sendMotofanBirhdays) {
		this.sendMotofanBirhdays = sendMotofanBirhdays;
	}

	public boolean isUseButtonCaptcha() {
		return useButtonCaptcha;
	}

	public void setUseButtonCaptcha(boolean useButtonCaptcha) {
		this.useButtonCaptcha = useButtonCaptcha;
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

	public int getDigestPageDeep() {
		return digestPageDeep;
	}

	public void setDigestPageDeep(int digestPageDeep) {
		this.digestPageDeep = digestPageDeep;
	}

	public long getObsoleteDataDelay() {
		return obsoleteDataDelay;
	}

	public void setObsoleteDataDelay(long obsoleteDataDelay) {
		this.obsoleteDataDelay = obsoleteDataDelay;
	}

	public boolean isDigestShredder() {
		return digestShredder;
	}

	public void setDigestShredder(boolean digestShredder) {
		this.digestShredder = digestShredder;
	}

	public int getCaptchaDelay() {
		return captchaDelay;
	}

	public void setCaptchaDelay(int captchaDelay) {
		this.captchaDelay = captchaDelay;
	}

	public int getCaptchaBan() {
		return captchaBan;
	}

	public void setCaptchaBan(int captchaBan) {
		this.captchaBan = captchaBan;
	}
}
