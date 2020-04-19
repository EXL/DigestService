package ru.exlmoto.digest.site.form;

import org.springframework.util.StringUtils;

public class SendForm {
	private Long sendChatId;
	private String sendChatArg;

	private Long stickerChatId;
	private String stickerChatArg;

	private Long imageChatId;
	private String imageChatArg;

	public boolean checkSend() {
		return sendChatId != null && StringUtils.hasText(sendChatArg);
	}

	public boolean checkSticker() {
		return stickerChatId != null && StringUtils.hasText(stickerChatArg);
	}

	public boolean checkImage() {
		return imageChatId != null && StringUtils.hasText(imageChatArg);
	}

	public Long getSendChatId() {
		return sendChatId;
	}

	public void setSendChatId(Long sendChatId) {
		this.sendChatId = sendChatId;
	}

	public String getSendChatArg() {
		return sendChatArg;
	}

	public void setSendChatArg(String sendChatArg) {
		this.sendChatArg = sendChatArg;
	}

	public Long getStickerChatId() {
		return stickerChatId;
	}

	public void setStickerChatId(Long stickerChatId) {
		this.stickerChatId = stickerChatId;
	}

	public String getStickerChatArg() {
		return stickerChatArg;
	}

	public void setStickerChatArg(String stickerChatArg) {
		this.stickerChatArg = stickerChatArg;
	}

	public Long getImageChatId() {
		return imageChatId;
	}

	public void setImageChatId(Long imageChatId) {
		this.imageChatId = imageChatId;
	}

	public String getImageChatArg() {
		return imageChatArg;
	}

	public void setImageChatArg(String imageChatArg) {
		this.imageChatArg = imageChatArg;
	}
}
