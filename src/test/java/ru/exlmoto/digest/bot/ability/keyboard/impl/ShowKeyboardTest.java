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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class ShowKeyboardTest {
	@Autowired
	private ShowKeyboard keyboard;

	@SpyBean
	private DatabaseService service;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private BotConfiguration config;

	@Autowired
	private LocaleHelper locale;
	
	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetKeyboard() {
		assertNotNull(keyboard.getMarkup(locale, config, 2, 2));
		assertNull(keyboard.getMarkup(locale, config, 1, 1));
		assertNull(keyboard.getMarkup(locale, config, 1, 0));
		assertNotNull(keyboard.getMarkup(locale, config, 3, 2));
		assertNull(keyboard.getMarkup(locale, config, 3, 1));
	}

	@Test
	public void testHandleQuery() {
		keyboard.handleQuery("Fake callbackId", update.getUser("exlmoto"), 1, sender, helper);
		keyboard.handleQuery("Fake callbackId", update.getUser("anyone"), 1, sender, helper);
	}

	@Test
	public void testHandle() {
		assertThrows(IllegalArgumentException.class, () -> keyboard.handle(0, update.getChat(),
			update.getUser("exlmoto"), 0, true, sender));

		keyboard.handle(0, update.getChat(), update.getUser("exlmoto"), 1, true, sender);

		doThrow(new InvalidDataAccessResourceUsageException("Test!")).when(service).getAllDigests(anyInt(), anyInt());

		keyboard.handle(0, update.getChat(), update.getUser("exlmoto"), 2, true, sender);
	}
}
