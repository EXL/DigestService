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

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(properties = "bot.silent=true")
class SubscribeKeyboardTest {
	@Autowired
	private SubscribeKeyboard keyboard;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup());
	}

	@Test
	public void testHandle() {
		/* Admin access error. */
		testHandleOnUsername("anyone");

		/* All ok. */
		testHandleOnUsername("exlmoto");
	}

	@Test
	public void testProcessSubscribeStatusMessage() {
		Chat chat = new Chat();
		setField(chat, "id", 0L);
		setField(chat, "title", "Chat Title");

		setField(chat, "type", Type.supergroup);
		keyboard.processSubscribeStatusMessage(0, chat, false, sender);
		keyboard.processSubscribeStatusMessage(0, chat, true, sender);

		setField(chat, "type", Type.Private);
		setField(chat, "username", "username");
		setField(chat, "title", null);
		keyboard.processSubscribeStatusMessage(0, chat, false, sender);
		keyboard.processSubscribeStatusMessage(0, chat, true, sender);
	}

	private void testHandleOnUsername(String username) {
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName(), username));
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName() + "key", username));
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName() + "digest_subscribe", username));
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName() + "digest_unsubscribe", username));
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName() + "motofan_subscribe", username));
		keyboard.execute(helper, sender, locale,
			callbackQueryHelper(Keyboard.subscribe.withName() + "motofan_unsubscribe", username));
	}

	private CallbackQuery callbackQueryHelper(String data, String username) {
		return update.getCallbackQueryUsername(data, username);
	}
}
