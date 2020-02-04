package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@SpringBootTest(properties = "bot.silent=true")
class DebugCommandTest {
	@Autowired
	private DebugCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDebugCommand() {
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug", "exlmoto"));
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug Unk", "exlmoto"));
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug Unk Unk", "exlmoto"));
	}

	@Test
	public void testDebugCommandOnSomeOptions() {
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug VStatus", "exlmoto"));
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug VQueries", "exlmoto"));
		command.execute(helper, sender, locale, update.getSimpleMessage("/debug BGreetings", "exlmoto"));
	}
}
