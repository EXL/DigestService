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
		User user = new User();
		setField(user, "first_name", "First");
		assertEquals("First", helper.getValidUsername(user));

		setField(user, "last_name", "Last");
		assertEquals("First Last", helper.getValidUsername(user));

		setField(user, "username", "User");
		assertEquals("@User", helper.getValidUsername(user));

		setField(user, "last_name", null);
		assertEquals("@User", helper.getValidUsername(user));
	}

	@Test
	public void testIsUserAdmin() {
		assertTrue(helper.isUserAdmin("exlmoto"));
		assertTrue(helper.isUserAdmin("ZorgeR"));
		assertFalse(helper.isUserAdmin("anyone"));
	}

	@Test
	public void testGetCurrentUnixTime() {
		String value = String.valueOf(helper.getCurrentUnixTime());
		assertEquals(10, value.length());
		System.out.println("Unix Time: " + value);
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
