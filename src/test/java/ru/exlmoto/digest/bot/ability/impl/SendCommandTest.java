package ru.exlmoto.digest.bot.ability.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = { "bot.silent=true", "image.download-file=false" })
class SendCommandTest {
	@Autowired
	private SendCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

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

	private void onCmd(String message) {
		command.execute(helper, sender, locale, new MessageHelper().getSimpleMessage(message, "exlmoto"));
	}
}
