/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "bot.silent=true")
class GreetingKeyboardTest {
	@Autowired
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

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup(false));
		assertNotNull(keyboard.getMarkup(true));
	}

	@Test
	public void testHandle() throws InterruptedException {
		/* Admin access error. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "anyone"));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "anyone"));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testHandleAdmin() throws InterruptedException {
		/* All ok. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);
	}

	@Test
	public void testProcessGreetingStatusMessage() throws InterruptedException {
		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, false, sender);
		Thread.sleep(1000);

		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, true, sender);
		Thread.sleep(1000);
	}
}
