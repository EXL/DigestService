/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.bot.util;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.User;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest
class BotHelperTest {
	@Autowired
	private BotHelper helper;

	@Test
	public void testGetValidUsername() {
		User user = new User(42L);
		setField(user, "first_name", "First");
		assertEquals("First", helper.getValidUsername(user));

		setField(user, "last_name", "Last");
		assertEquals("First Last", helper.getValidUsername(user));

		setField(user, "username", "User");
		assertEquals("@User", helper.getValidUsername(user));

		setField(user, "last_name", null);
		assertEquals("@User", helper.getValidUsername(user));

		setField(user, "username", null);
		assertEquals("First", helper.getValidUsername(user));

		setField(user, "first_name", "@First");
		assertEquals("AFirst", helper.getValidUsername(user));

		setField(user, "last_name", "@Last");
		assertEquals("AFirst ALast", helper.getValidUsername(user));

		setField(user, "first_name", "@rt@mk@");
		setField(user, "last_name", "@L@st");
		assertEquals("Art@mk@ AL@st", helper.getValidUsername(user));
	}

	@Test
	public void testIsUserAdmin() {
		assertTrue(helper.isUserAdmin("exlmoto"));
		assertTrue(helper.isUserAdmin("ZorgeR"));
		assertFalse(helper.isUserAdmin("anyone"));
	}

	@Test
	public void testValidChatName() {
		Chat chat = new Chat();
		setField(chat, "type", Chat.Type.Private);
		setField(chat, "username", "username");
		assertEquals("@username", helper.getValidChatName(chat));

		setField(chat, "title", "title-chat");
		assertEquals("@username", helper.getValidChatName(chat));

		setField(chat, "type", Type.channel);
		assertEquals("title-chat", helper.getValidChatName(chat));

		setField(chat, "type", Type.group);
		assertEquals("title-chat", helper.getValidChatName(chat));

		setField(chat, "type", Type.supergroup);
		assertEquals("title-chat", helper.getValidChatName(chat));
	}
}
