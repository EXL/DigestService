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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendFormUnitTest {
	private final SendForm form = new SendForm();

	@Test
	public void testCheckSend() {
		assertFalse(form.checkSend());
		form.setSendChatId(0L);
		assertFalse(form.checkSend());
		form.setSendChatArg("");
		assertFalse(form.checkSend());
		form.setSendChatArg(" ");
		assertFalse(form.checkSend());
		form.setSendChatArg("value");
		assertTrue(form.checkSend());
	}

	@Test
	public void testCheckSticker() {
		assertFalse(form.checkSticker());
		form.setStickerChatId(0L);
		assertFalse(form.checkSticker());
		form.setStickerChatArg("");
		assertFalse(form.checkSticker());
		form.setStickerChatArg(" ");
		assertFalse(form.checkSticker());
		form.setStickerChatArg("value");
		assertTrue(form.checkSticker());
	}

	@Test
	public void testCheckImage() {
		assertFalse(form.checkImage());
		form.setImageChatId(0L);
		assertFalse(form.checkImage());
		form.setImageChatArg("");
		assertFalse(form.checkImage());
		form.setImageChatArg(" ");
		assertFalse(form.checkImage());
		form.setImageChatArg("value");
		assertTrue(form.checkImage());
	}
}
