package ru.exlmoto.digest.bot.ability.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.message.impl.CoffeeCommand;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = "bot.silent=true")
class CoffeeCommandTest {
	@Autowired
	private CoffeeCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testCoffeeCommand() {
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/coffee", "anyone"));
	}
}
