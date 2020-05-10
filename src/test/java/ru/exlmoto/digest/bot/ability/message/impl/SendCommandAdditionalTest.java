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

@SpringBootTest(properties = "image.download-file=true")
class SendCommandAdditionalTest {
	@Autowired
	private SendCommand command;

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
		when(config.isSilent()).thenReturn(false);
	}

	@Test
	public void testOnWrongCommand() {
		/* Wrong command. */
		onCmd("/test error");
	}

	@Test
	public void testSendOnWrongChats() {
		/* Wrong chat. */
		onCmd("/send 87123336977 ad");
		onCmd("/sticker 87123336977 CAADAgADzAEAAhGoNAVFRRJu94qe3gI");
		onCmd("/image 87123336977 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png");
	}

	@Test
	public void testSendOnImageError() {
		/* Image error. */
		onCmd("/image 87336977 https://exlmoto.ru");
		onCmd("/image 87336977 https://exlmotor.ru");
		onCmd("/image 87336977 https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
	}

	private void onCmd(String message) {
		command.execute(helper, sender, locale, update.getSimpleMessage(message, "exlmoto"));
	}
}
