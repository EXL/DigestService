package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.Chat;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(properties = "bot.silent=true")
class GreetingKeyboardTest {
	@Autowired
	private BotConfiguration config;

	@Autowired
	private GreetingKeyboard keyboard;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetMarkup() {
		assertNotNull(keyboard.getMarkup(false));
		assertNotNull(keyboard.getMarkup(true));
	}

	@Test
	public void testHandle() throws InterruptedException {
		/* Admin access error. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "anyone"));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "anyone"));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "anyone"));
		Thread.sleep(1000);
	}

	@Test
	public void testHandleAdmin() throws InterruptedException {
		/* All ok. */
		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "on", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "off", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);

		keyboard.execute(helper, sender, locale,
			update.getCallbackQueryUsername(Keyboard.greeting.withName() + "wtf", "exlmoto", 87336977,
				config.getMotofanChatId()));
		Thread.sleep(1000);
	}

	@Test
	public void testProcessGreetingStatusMessage() throws InterruptedException {
		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, false, sender);
		Thread.sleep(1000);

		keyboard.processGreetingStatusMessage(config.getMotofanChatId(), 0, true, sender);
		Thread.sleep(1000);
	}
}
