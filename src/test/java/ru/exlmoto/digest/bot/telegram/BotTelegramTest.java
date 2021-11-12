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

package ru.exlmoto.digest.bot.telegram;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.response.GetChatAdministratorsResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.when;

@SpringBootTest
class BotTelegramTest {
	@Autowired
	private BotTelegram telegram;

	@SpyBean
	private BotConfiguration config;

	@BeforeEach
	public void setUpTests() {
		when(config.isSilent()).thenReturn(true);
	}

	@Test
	public void testGetBot() {
		assertNotNull(telegram.getBot());
	}

	@Test
	public void testProcessTelegramBotSettings() {
		System.out.println("=== START testProcessTelegramBotSettings() ===");
		telegram.createTelegramBotSettings();
		telegram.updateTelegramBotSettings();
		System.out.println("=== END testProcessTelegramBotSettings() ===");
	}

	@Test
	public void testBotParameters() {
		System.out.println("=== START testBotParameters() ===");
		String name = telegram.getFirstName();
		String username = telegram.getUsername();
		long id = telegram.getId();
		String token = config.getToken();

		assertThat(name).isNotEmpty();
		assertThat(username).isNotEmpty();
		assertThat(id).isNotZero();
		assertThat(token).isNotEmpty();

		System.out.println(name);
		System.out.println(username);
		System.out.println(id);
		System.out.println(token);
		System.out.println("=== END testBotParameters() ===");
	}

	@Test
	public void testChatAdministrators() {
		assertFalse(adminListHelper(telegram.chatAdministrators(config.getMotofanChatId())).isEmpty());
		assertTrue(adminListHelper(telegram.chatAdministrators(-100104511784134569L)).isEmpty());
		assertTrue(adminListHelper(telegram.chatAdministrators(87336977L)).isEmpty());
	}

	private List<ChatMember> adminListHelper(GetChatAdministratorsResponse response) {
		if (response != null && response.isOk()) {
			return response.administrators();
		}
		return new ArrayList<>();
	}
}
