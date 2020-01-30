package ru.exlmoto.digest.bot.ability.message;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.message.impl.HelloCommand;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = "bot.silent=true")
class MessageAdminAbilityTest {
	@Autowired
	private HelloCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testProcessAux() {
		command.process(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/hi", "exlmoto"));

		command.process(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/hi", "anyone"));
	}
}
