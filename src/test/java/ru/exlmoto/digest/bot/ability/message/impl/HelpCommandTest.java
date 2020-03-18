package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@SpringBootTest(properties = "bot.silent=true")
class HelpCommandTest {
	@Autowired
	private BotConfiguration config;

	@Autowired
	private HelpCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testHelpCommand() throws InterruptedException {
		command.execute(helper, sender, locale, update.getSimpleMessage("/help", "anyone"));
		Thread.sleep(1000);

		command.execute(helper, sender, locale,
			update.getSimpleMessageAdmin("/help", "anyone", config.getMotofanChatId()));
		Thread.sleep(1000);

		command.execute(helper, sender, locale, update.getSimpleMessage("/help", "exlmoto"));
		Thread.sleep(1000);

		command.execute(helper, sender, locale,
			update.getSimpleMessageAdmin("/help", "exlmoto", config.getMotofanChatId()));
		Thread.sleep(1000);
	}
}
