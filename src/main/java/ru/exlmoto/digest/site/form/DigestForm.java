package ru.exlmoto.digest.site.form;

public class DigestForm {
	private boolean update;

	private Long digestId;
	private Long chatId;
	private Long date;
	private Long messageId;
	private Long userId;
	private String digest;

	public boolean checkForm() {
		return chatId != null && date != null && digest != null && userId != null;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public Long getDigestId() {
		return digestId;
	}

	public void setDigestId(Long digestId) {
		this.digestId = digestId;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	@Override
	public String toString() {
		return
			"DigestForm{update=" + update +
			", digestId=" + digestId +
			", chatId=" + chatId +
			", date=" + date +
			", messageId=" + messageId +
			", userId=" + userId +
			", digest=" + digest +
			"}";
	}
}
