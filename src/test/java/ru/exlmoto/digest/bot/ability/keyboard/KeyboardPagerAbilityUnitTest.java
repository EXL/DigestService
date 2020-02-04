package ru.exlmoto.digest.bot.ability.keyboard;

import com.pengrad.telegrambot.model.User;

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyboardPagerAbilityUnitTest {

	private final KeyboardPagerAbility keyboard = new KeyboardPagerAbility() {
		@Override
		protected Keyboard getKeyboard() {
			return Keyboard.show;
		}

		@Override
		protected boolean handleQuery(String callbackId, User user, int page,
		                              BotSender sender, BotHelper helper, LocaleHelper locale) {
			return false;
		}

		@Override
		public void handle(long chatId, int messageId, User user, int page, boolean edit, BotSender sender) {

		}
	};
	
	@Test
	public void testGetPageFromArgument() {
		assertEquals(1, keyboard.getPageFromArgument("0"));
		assertEquals(1, keyboard.getPageFromArgument("1"));
		assertEquals(2, keyboard.getPageFromArgument("2"));
		assertEquals(10, keyboard.getPageFromArgument("10"));
		assertEquals(11, keyboard.getPageFromArgument("11"));
		assertEquals(1, keyboard.getPageFromArgument("asd"));
		assertEquals(1, keyboard.getPageFromArgument("-1"));
		assertEquals(1, keyboard.getPageFromArgument("-3"));
		assertEquals(1, keyboard.getPageFromArgument("1232131231231"));
	}

	@Test
	public void testCallbackPageData() {
		assertEquals("show_page_1", keyboard.callbackPageData(1));
		assertEquals("show_page_2", keyboard.callbackPageData(2));
		assertEquals("show_page_10", keyboard.callbackPageData(10));
		assertEquals("show_page_-1", keyboard.callbackPageData(-1));
	}
}
