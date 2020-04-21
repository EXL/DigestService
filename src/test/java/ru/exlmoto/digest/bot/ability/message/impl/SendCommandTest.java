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

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = { "bot.silent=true", "image.download-file=false" })
class SendCommandTest {
	@Autowired
	private SendCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testSendCommand() {
		/* Wrong format. */
		onCmd("/send");
		onCmd("/send wrong-id");
		onCmd("/send 87336977");
		System.out.println("===");

		/* Wrong id. */
		onCmd("/send wrong-id first");
		onCmd("/send wrong-id first second");
		System.out.println("===");

		/* Ok. */
		onCmd("/send 87336977 text");
		onCmd("/send 87336977 text text");
		onCmd("/send -1001148683293 text");
		onCmd("/send -1001148683293 text text");
		System.out.println("===");
	}

	@Test
	public void testStickerCommand() {
		/* Wrong format. */
		onCmd("/sticker");
		onCmd("/sticker wrong-id");
		onCmd("/sticker wrong-id CAADAgADzAEAAhGoNAVFRRJu94qe3gI text");
		onCmd("/sticker 87336977");
		onCmd("/sticker 87336977 CAADAgADzAEAAhGoNAVFRRJu94qe3gI text");
		onCmd("/sticker -1001148683293 CAADAgADzAEAAhGoNAVFRRJu94qe3gI text");
		System.out.println("===");

		/* Wrong id. */
		onCmd("/sticker wrong-id CAADAgADzAEAAhGoNAVFRRJu94qe3gI");
		System.out.println("===");

		/* Ok. */
		onCmd("/sticker 87336977 CAADAgADzAEAAhGoNAVFRRJu94qe3gI");
		onCmd("/sticker -1001148683293 CAADAgADzAEAAhGoNAVFRRJu94qe3gI");
		System.out.println("===");
	}

	@Test
	public void testImageCommand() {
		/* Wrong format. */
		onCmd("/image");
		onCmd("/image wrong-id");
		onCmd("/image wrong-id https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png text");
		onCmd("/image 87336977");
		onCmd("/image 87336977 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png text");
		onCmd("/image -1001148683293 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png text");
		System.out.println("===");

		/* Wrong id. */
		onCmd("/image wrong-id https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png");
		System.out.println("===");

		/* Ok. */
		onCmd("/image 87336977 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png");
		onCmd("/image -1001148683293 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png");
		System.out.println("===");
	}

	@Test
	public void testIsolateCommand() {
		assertEquals("send", command.isolateCommand("/send"));
		assertEquals("send", command.isolateCommand("/send@Somebot"));
		assertEquals("send", command.isolateCommand("/send@Somebot_test"));

		assertEquals("sticker", command.isolateCommand("/sticker"));
		assertEquals("sticker", command.isolateCommand("/sticker@Somebot"));
		assertEquals("sticker", command.isolateCommand("/sticker@Somebot_test"));

		assertEquals("image", command.isolateCommand("/image"));
		assertEquals("image", command.isolateCommand("/image@Somebot"));
		assertEquals("image", command.isolateCommand("/image@Somebot_test"));
	}

	private void onCmd(String message) {
		command.execute(helper, sender, locale, update.getSimpleMessage(message, "exlmoto"));
	}
}
