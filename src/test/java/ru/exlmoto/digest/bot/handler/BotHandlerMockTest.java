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

package ru.exlmoto.digest.bot.handler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.service.DatabaseService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "bot.silent=true")
public class BotHandlerMockTest {
	@Autowired
	private BotHandler handler;

	@MockBean
	private DatabaseService service;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDisableGreetingsByGroup() {
		when(service.checkGreeting(anyLong())).thenReturn(false);

		/* No output. */
		handler.onNewUsers(update.getNewUsers(4));
		handler.onLeftUser(update.getLeftUser("Left"));
		handler.onNewPhotos(update.getNewPhotos(4));
	}
}
