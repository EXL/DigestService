package ru.exlmoto.digest.bot.ability.message;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.message.impl.GreetingCommand;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@SpringBootTest(properties = "bot.silent=true")
class MessageModerAbilityTest {
	@Autowired
	private BotConfiguration config;

	@Autowired
	private GreetingCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testProcessAux() throws InterruptedException {
		command.process(helper, sender, locale, update.getSimpleMessage("/greeting", "anyone"));
		Thread.sleep(2000);

		command.process(helper, sender, locale,
			update.getSimpleMessageAdmin("/greeting", "exlmoto", config.getMotofanChatId()));
		Thread.sleep(2000);
	}
}
