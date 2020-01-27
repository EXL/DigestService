package ru.exlmoto.digest.bot.ability.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = { "bot.silent=false", "image.download-file=true" })
class SendCommandAdditionalTest {
	@Autowired
	private SendCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testOnWrongCommand() {
		/* Wrong command */
		onCmd("/test error");
	}

	@Test
	public void testSendOnWrongChats() {
		/* Wrong chat */
		onCmd("/send 87123336977 ad");
		onCmd("/sticker 87123336977 CAADAgADzAEAAhGoNAVFRRJu94qe3gI");
		onCmd("/image 87123336977 https://exlmoto.ru/wp-content/Images/PERL1987/ArchLinux_twm_Perl_1987_1.png");
	}

	@Test
	public void testSendOnImageError() {
		/* Image error */
		onCmd("/image 87336977 https://exlmoto.ru");
		onCmd("/image 87336977 https://exlmotor.ru");
		onCmd("/image 87336977 https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
	}

	private void onCmd(String message) {
		command.execute(helper, sender, locale, new MessageHelper().getSimpleMessage(message, "exlmoto"));
	}
}
