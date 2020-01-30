package ru.exlmoto.digest.bot.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.ability.keyboard.impl.SubscribeKeyboard;
import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.CallbackQueryHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.util.ReflectionTestUtils.setField;

@SpringBootTest(properties = "bot.silent=true")
class SubscribeKeyboardTest {
	@Autowired
	private SubscribeKeyboard keyboard;

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
		/* Admin access error. */
		testHandleOnUsername("anyone");

		/* All ok. */
		testHandleOnUsername("exlmoto");
	}

	@Test
	public void testProcessSubscribeStatusMessage() {
		Chat chat = new Chat();
		setField(chat, "id", 0L);
		setField(chat, "title", "Chat Title");

		setField(chat, "type", Type.supergroup);
		keyboard.processSubscribeStatusMessage(0L, 0, chat, false, sender);
		keyboard.processSubscribeStatusMessage(0L, 0, chat, true, sender);

		setField(chat, "type", Type.Private);
		keyboard.processSubscribeStatusMessage(0L, 0, chat, false, sender);
		keyboard.processSubscribeStatusMessage(0L, 0, chat, true, sender);
	}

	private void testHandleOnUsername(String username) {
		keyboard.handle(helper, sender, locale, callbackQueryHelper(BotKeyboard.SUBSCRIBE, username));
		keyboard.handle(helper, sender, locale,
			callbackQueryHelper(BotKeyboard.SUBSCRIBE + "key", username));
		keyboard.handle(helper, sender, locale,
			callbackQueryHelper(BotKeyboard.SUBSCRIBE + "digest_subscribe", username));
		keyboard.handle(helper, sender, locale,
			callbackQueryHelper(BotKeyboard.SUBSCRIBE + "digest_unsubscribe", username));
		keyboard.handle(helper, sender, locale,
			callbackQueryHelper(BotKeyboard.SUBSCRIBE + "motofan_subscribe", username));
		keyboard.handle(helper, sender, locale,
			callbackQueryHelper(BotKeyboard.SUBSCRIBE + "motofan_unsubscribe", username));
	}

	private CallbackQuery callbackQueryHelper(String data, String username) {
		return new CallbackQueryHelper().getCallbackQueryUsername(data, username);
	}
}
