package ru.exlmoto.digest.bot.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.CallbackQueryHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

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
	private LocalizationHelper locale;

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup());
	}

	@Test
	public void testHandle() {
		keyboard.handle(helper, sender, locale, new CallbackQueryHelper().getCallbackQuery("chart_"));
		keyboard.handle(helper, sender, locale, new CallbackQueryHelper().getCallbackQuery("chart_key"));
		keyboard.handle(helper, sender, locale, new CallbackQueryHelper().getCallbackQuery("chart_usd_rub"));
	}
}
