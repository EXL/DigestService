package ru.exlmoto.digest.site.form;

import javax.validation.constraints.NotNull;

public class SubscriberForm {
	private boolean showName;

	@NotNull
	private Long chatId;
	private String chatName;

	public boolean isShowName() {
		return showName;
	}

	public void setShowName(boolean showName) {
		this.showName = showName;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public String getChatName() {
		return chatName;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}
}
