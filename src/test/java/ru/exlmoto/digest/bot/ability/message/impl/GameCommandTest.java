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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.mockito.Mockito.when;

@SpringBootTest(properties = "image.download-file=false")
class GameCommandTest {
	@Autowired
	private GameCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	@SpyBean
	private BotConfiguration config;

	private final UpdateHelper update = new UpdateHelper();

	@BeforeEach
	public void setUpTests() {
		when(config.isSilent()).thenReturn(true);
	}

	@Test
	public void testGameCommand() {
		System.out.println("=== START testGameCommand() ===");
		command.execute(helper, sender, locale, update.getSimpleMessage("/game", "anyone"));
		System.out.println("=== END testGameCommand() ===");
	}

	@Test
	public void testParseInfoQuake2() {
		System.out.println("=== START testParseInfoQuake2() ===");
		String answer = "����info\n   q2.exlmoto.ru    base1  0/ 4";
		System.out.println(command.parseInfoQuake2(locale, answer));
		System.out.println("=== END testParseInfoQuake2() ===");
	}

	@Test
	public void testGenerateTable() {
		System.out.println("=== START testGenerateTable() ===");

		String[] tokens = new String[] { "q2.exlmoto.ru", "base1", "0/4" };
		System.out.println(command.generateTable(locale, tokens));

		tokens = new String[] { "q2.exlmoto.ru", "aaaaaaaaabase1", "0/128" };
		System.out.println(command.generateTable(locale, tokens));

		System.out.println("=== END testGenerateTable() ===");
	}
}
