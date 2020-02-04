package ru.exlmoto.digest.bot.ability.keyboard.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Pageable;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class ShowKeyboardTest {
	@Autowired
	private ShowKeyboard keyboard;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private BotConfiguration config;

	@Autowired
	private LocaleHelper locale;
	
	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGetKeyboard() {
		assertNotNull(keyboard.getMarkup(locale, config, 2, 2));
		assertNull(keyboard.getMarkup(locale, config, 1, 1));
		assertNull(keyboard.getMarkup(locale, config, 1, 0));
		assertNotNull(keyboard.getMarkup(locale, config, 3, 2));
		assertNull(keyboard.getMarkup(locale, config, 3, 1));
	}

	@Test
	public void testHandleQuery() {
		keyboard.handleQuery("Fake callbackId", update.getUser("exlmoto"), 1, sender, helper);
		keyboard.handleQuery("Fake callbackId", update.getUser("anyone"), 1, sender, helper);
	}

	@Test
	public void testHandle() {
		assertThrows(IllegalArgumentException.class, () -> keyboard.handle(0, update.getChat(),
			update.getUser("exlmoto"), 0, true, sender));

		keyboard.handle(0, update.getChat(), update.getUser("exlmoto"), 1, true, sender);

		doThrow(new InvalidDataAccessResourceUsageException("Test!"))
			.when(botDigestRepository).findAll(any(Pageable.class));

		keyboard.handle(0, update.getChat(), update.getUser("exlmoto"), 2, true, sender);
	}

	@Test
	public void testArrangeString() {
		assertEquals("i     ", keyboard.arrangeString("i", 6));
		assertEquals("id    ", keyboard.arrangeString("id", 6));
		assertEquals("post  ", keyboard.arrangeString("post", 6));
		assertEquals("post-post", keyboard.arrangeString("post-post", 6));
		assertEquals("id", keyboard.arrangeString("id", 0));
		assertEquals("id", keyboard.arrangeString("id", 1));
		assertEquals("id", keyboard.arrangeString("id", 2));
		assertEquals("id ", keyboard.arrangeString("id", 3));
		assertEquals("id", keyboard.arrangeString("id", -1));
	}

	@Test
	public void testEllipsisString() {
		assertEquals("usern...",
			keyboard.ellipsisString("username", 6, "...", true));
		assertEquals("...rname",
			keyboard.ellipsisString("username", 6, "...", false));

		assertEquals("user...",
			keyboard.ellipsisString("username", 5, "...", true));
		assertEquals("...name",
			keyboard.ellipsisString("username", 5, "...", false));

		assertEquals("Laser   ",
			keyboard.ellipsisString("Laser", 8, "...", true));
		assertEquals("Laser   ",
			keyboard.ellipsisString("Laser", 8, "...", false));

		assertEquals("Laser At...",
			keyboard.ellipsisString("Laser Attack", 9, "...", true));
		assertEquals("...r Attack",
			keyboard.ellipsisString("Laser Attack", 9, "...", false));

		assertEquals("...",
			keyboard.ellipsisString("Laser Attack", 1, "...", true));
		assertEquals("...",
			keyboard.ellipsisString("Laser Attack", 1, "...", false));

		assertEquals("Laser Attack",
			keyboard.ellipsisString("Laser Attack", 0, "...", true));
		assertEquals("Laser Attack",
			keyboard.ellipsisString("Laser Attack", 0, "...", false));

		assertEquals("Laser Attack",
			keyboard.ellipsisString("Laser Attack", -1, "...", true));
		assertEquals("Laser Attack",
			keyboard.ellipsisString("Laser Attack", -1, "...", false));
	}
}
