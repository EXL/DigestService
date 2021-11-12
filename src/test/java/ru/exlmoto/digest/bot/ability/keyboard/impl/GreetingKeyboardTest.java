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

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.when;

@SpringBootTest
class GreetingKeyboardTest {
	@SpyBean
	private BotConfiguration config;

	@Autowired
	private GreetingKeyboard keyboard;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@BeforeEach
	public void setUpTests() {
		when(config.isSilent()).thenReturn(true);
	}

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup(false));
		assertNotNull(keyboard.getMarkup(true));
	}

	@Test
	public void testHandle() {
		System.out.println("=== START testHandle() ===");
		/* Admin access error. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "anyone"));
		System.out.println("---");
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "anyone"));
		System.out.println("---");
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "anyone"));
		System.out.println("=== END testHandle() ===");
	}

	@Test
	public void testHandleAdmin() {
		System.out.println("=== START testHandleAdmin() ===");
		/* All ok. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "exlmoto", 87336977L,
				config.getMotofanChatId()));
		System.out.println("---");
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "exlmoto", 87336977L,
				config.getMotofanChatId()));
		System.out.println("---");
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "exlmoto", 87336977L,
				config.getMotofanChatId()));
		System.out.println("=== END testHandleAdmin() ===");
	}

	@Test
	public void testProcessGreetingStatusMessage() {
		System.out.println("=== START testProcessGreetingStatusMessage() ===");
		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, false, sender);
		System.out.println("---");
		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, true, sender);
		System.out.println("=== END testProcessGreetingStatusMessage() ===");
	}
}
