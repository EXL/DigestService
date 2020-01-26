package ru.exlmoto.digest.bot.ability.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@SpringBootTest(properties = { "bot.silent=true", "image.download-file=false" })
class GameCommandTest {
	@Autowired
	private GameCommand command;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testGameCommand() {
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/game", "anyone"));
	}
}