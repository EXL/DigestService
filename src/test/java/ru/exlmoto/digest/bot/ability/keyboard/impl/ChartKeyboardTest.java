package ru.exlmoto.digest.bot.ability.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "bot.silent=true")
class ChartKeyboardTest {
	@Autowired
	private ChartKeyboard keyboard;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;
	
	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup());
	}

	@Test
	public void testHandle() {
		keyboard.execute(helper, sender, locale, update.getCallbackQuery(Keyboard.chart.withName()));
		keyboard.execute(helper, sender, locale, update.getCallbackQuery(Keyboard.chart.withName() + "key"));
		keyboard.execute(helper, sender, locale, update.getCallbackQuery(Keyboard.chart.withName() + "usd_rub"));
	}
}
