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

package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "bot.silent=true")
class DigestHashTagTest {
	@Autowired
	private DigestHashTag hashTag;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@MockBean
	private BotDigestUserRepository botDigestUserRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDigestHashTag() {
		hashTag.execute(helper, sender, locale, update.getSimpleMessage("#digest Test!", "anyone"));
		hashTag.execute(helper, sender, locale, update.getSimpleMessage("#news Test!", "anyone"));
		hashTag.execute(helper, sender, locale, update.getSimpleMessage("#digest  ", "anyone"));
		hashTag.execute(helper, sender, locale, update.getSimpleMessage("#news", "anyone"));
	}

	@Test
	public void testDigestHashTagLong() {
		StringBuilder longMessage = new StringBuilder("#news");
		for (int i = 0; i < 100; i++) {
			longMessage.append(" ").append("check");
		}
		hashTag.execute(helper, sender, locale, update.getSimpleMessage(longMessage.toString(), "anyone"));
	}

	@Test
	public void testIsolateMessageText() {
		assertEquals("check", hashTag.isolateMessageText("#digest  check"));
		assertEquals("check", hashTag.isolateMessageText("#news  check"));

		assertEquals("", hashTag.isolateMessageText("#digest"));
		assertEquals("", hashTag.isolateMessageText("#news"));

		assertEquals("ddd check ad", hashTag.isolateMessageText("ddd  #digest  check <b>ad</b>  "));
		assertEquals("ddd check ad", hashTag.isolateMessageText("ddd  #news  check <b>ad</b>  "));
	}
}
