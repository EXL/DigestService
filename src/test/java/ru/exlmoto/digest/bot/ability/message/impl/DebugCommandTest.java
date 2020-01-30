package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = "bot.silent=true")
class DebugCommandTest {
	@Autowired
	private DebugCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testDebugCommand() {
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug", "exlmoto"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug Unknown", "exlmoto"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug Unknown Unknown", "exlmoto"));
	}

	@Test
	public void testDebugCommandOnSomeOptions() {
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug VStatus", "exlmoto"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug VQueries", "exlmoto"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/debug BGreetings", "exlmoto"));
	}
}
