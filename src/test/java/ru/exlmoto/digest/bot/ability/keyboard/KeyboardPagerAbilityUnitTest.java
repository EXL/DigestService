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

package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyboardPagerAbilityUnitTest {
	private final KeyboardPagerAbility keyboard = new KeyboardPagerAbility() {
		@Override
		protected Keyboard getKeyboard() {
			return Keyboard.show;
		}

		@Override
		protected boolean handleQuery(String callbackId, User user, int page, BotSender sender, BotHelper helper) {
			return false;
		}

		@Override
		public void handle(int messageId, Chat chat, User user, int page, boolean edit, BotSender sender) {

		}
	};

	@Test
	public void testGetPageFromArgument() {
		assertEquals(1, keyboard.getPageFromArgument("0"));
		assertEquals(1, keyboard.getPageFromArgument("1"));
		assertEquals(2, keyboard.getPageFromArgument("2"));
		assertEquals(10, keyboard.getPageFromArgument("10"));
		assertEquals(11, keyboard.getPageFromArgument("11"));
		assertEquals(1, keyboard.getPageFromArgument("asd"));
		assertEquals(1, keyboard.getPageFromArgument("-1"));
		assertEquals(1, keyboard.getPageFromArgument("-3"));
		assertEquals(1, keyboard.getPageFromArgument("1232131231231"));
	}

	@Test
	public void testCallbackPageData() {
		assertEquals("show_page_1", keyboard.callbackPageData(1));
		assertEquals("show_page_2", keyboard.callbackPageData(2));
		assertEquals("show_page_10", keyboard.callbackPageData(10));
		assertEquals("show_page_-1", keyboard.callbackPageData(-1));
	}
}
