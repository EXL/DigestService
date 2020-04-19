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

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "bot.silent=true")
class BotHandlerTest {
	@Autowired
	private BotHandler handler;

	@Autowired
	private BotConfiguration config;

	@Autowired
	private BotTelegram telegram;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@MockBean
	private BotDigestUserRepository botDigestUserRepository;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testOnCommand() throws InterruptedException {
		handler.onCommand(update.getCommand("/start", "exlmoto"));
		Thread.sleep(1000);
		handler.onCommand(update.getCommand("/hi", "exlmoto"));
		Thread.sleep(1000);
		handler.onCommand(update.getCommand("/hi", "anyone"));
		Thread.sleep(1000);
		handler.onCommand(update.getCommand("/unknown", "anyone"));
		Thread.sleep(1000);

		handler.onCommand(update.getCommand("gibberish /start gibberish", "exlmoto"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnHashTag() throws InterruptedException {
		handler.onHashTag(update.getHashTag("#news test", 0, 5, "exlmoto"));
		Thread.sleep(1000);
		handler.onHashTag(update.getHashTag("#digest test", 0, 7, "exlmoto"));
		Thread.sleep(1000);
		handler.onHashTag(update.getHashTag("#news test", 0, 5, "anyone"));
		Thread.sleep(1000);
		handler.onHashTag(update.getHashTag("#digest test", 0, 7 ,"anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnHashTagEmpty() throws InterruptedException {
		handler.onHashTag(update.getHashTag("#digest", 0, 7, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getHashTag("#news", 0, 5, "anyone"));
		Thread.sleep(1000);

		assertThrows(NullPointerException.class, () ->
			handler.onHashTag(update.getSimpleMessage("#digest#news", "anyone")));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("#digest #news",
			0, 7, 8, 5, "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnHashTagTwice() throws InterruptedException {
		handler.onHashTag(update.getTwoHashTags("#digest #news test",
			0, 7, 8, 5, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("#new #digest test",
			0, 4, 5, 7, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("#news #digest test",
			0, 5, 6, 7, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("#digest #digest test",
			0, 7, 8, 7, "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnHashTagMiddle() throws InterruptedException {
		handler.onHashTag(update.getHashTag("test #digest asd", 5, 7, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getHashTag("test #news asd", 5, 5, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("test #digest #news asd",
			5, 7, 13, 5, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("test #news #digest asd",
			5, 5, 11, 7, "anyone"));
		Thread.sleep(1000);

		handler.onHashTag(update.getTwoHashTags("test #digest #digest asd",
			5, 7, 13, 7, "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnHashTagCastsAnsLinks() throws InterruptedException {
		assertThrows(NullPointerException.class, () ->
			handler.onHashTag(update.getSimpleMessage("https://exlmoto.ru/manual#digest", "anyone")));
		Thread.sleep(1000);

		handler.onHashTag(update.getHashTag("#digest Check nicknames: @exlmoto @ZorgeR",
			0, 7, "exlmoto"));
		Thread.sleep(1000);

		handler.onHashTag(
			update.getHashTag("#digest Check links: https://exlmoto.ru tg://t.me/exlmoto file:///usr/local/",
				0, 7, "exlmoto")
		);
		Thread.sleep(1000);

		handler.onHashTag(update.getHashTag("#digest Check nickname and links: @exlmoto и https://exlmoto.ru",
			0, 7, "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testOnCallbackQuery() throws InterruptedException {
		handler.onCallbackQuery(update.getCallbackQueryUsername("show_page_1", "anyone"));
		Thread.sleep(config.getCooldown() * 1000);
		handler.onCallbackQuery(update.getCallbackQueryUsername("show_page_2", "exlmoto"));
		Thread.sleep(config.getCooldown() * 1000);
	}

	@Test
	public void testOnKeyboard() throws InterruptedException {
		handler.onKeyboard(update.getCallbackQueryUsername("unknown", "anyone"));
		Thread.sleep(config.getCooldown() * 1000);
		handler.onKeyboard(update.getCallbackQueryUsername("digest", "anyone"));
		Thread.sleep(config.getCooldown() * 1000);

		handler.onKeyboard(update.getCallbackQueryUsername("digest_1", "anyone"));
		Thread.sleep(config.getCooldown() * 1000);
		handler.onKeyboard(update.getCallbackQueryUsername("chart_usd_rub", "anyone"));
		Thread.sleep(config.getCooldown() * 1000);
	}

	@Test
	public void testOnNewUsers() {
		handler.onNewUsers(update.getNewUsers(1));
		handler.onNewUsers(update.getNewUsers(2));
		handler.onNewUsers(update.getNewUsers(3));
		handler.onNewUsers(update.getNewUsers(4));
		System.out.println("---");
		for (int i = 0; i < 20; i++) {
			handler.onNewUsers(update.getNewUsersWithUsername(telegram.getUsername().substring(1)));
		}
		System.out.println("---");
	}

	@Test
	public void testOnLeftUser() {
		handler.onLeftUser(update.getLeftUser("Left"));
		System.out.println("---");
		/* Empty, no send. */
		handler.onLeftUser(update.getLeftUser(telegram.getUsername().substring(1)));
		System.out.println("---");
	}

	@Test
	public void testOnNewPhotos() {
		handler.onNewPhotos(update.getNewPhotos(1));
		handler.onNewPhotos(update.getNewPhotos(2));
		handler.onNewPhotos(update.getNewPhotos(3));
		handler.onNewPhotos(update.getNewPhotos(4));
	}
}
