/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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
