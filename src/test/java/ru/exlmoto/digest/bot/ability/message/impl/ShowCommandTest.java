package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Pageable;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class ShowCommandTest {
	@Autowired
	private ShowCommand command;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	@Test
	public void testShowCommand() {
		doThrow(new InvalidDataAccessResourceUsageException("Test!"))
			.when(botDigestRepository).findAll(any(Pageable.class));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/show", "anyone"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/show 1", "anyone"));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/show asd", "anyone"));
	}

	@Test
	public void testGetPageFromArgument() {
		assertEquals(0, command.getPageFromArgument("0"));
		assertEquals(0, command.getPageFromArgument("1"));
		assertEquals(1, command.getPageFromArgument("2"));
		assertEquals(9, command.getPageFromArgument("10"));
		assertEquals(10, command.getPageFromArgument("11"));
		assertEquals(0, command.getPageFromArgument("asd"));
		assertEquals(0, command.getPageFromArgument("-1"));
		assertEquals(0, command.getPageFromArgument("-3"));
		assertEquals(0, command.getPageFromArgument("1232131231231"));
	}

	@Test
	public void testArrangeString() {
		assertEquals("i     ", command.arrangeString("i", 6));
		assertEquals("id    ", command.arrangeString("id", 6));
		assertEquals("post  ", command.arrangeString("post", 6));
		assertEquals("post-post", command.arrangeString("post-post", 6));
		assertEquals("id", command.arrangeString("id", 0));
		assertEquals("id", command.arrangeString("id", 1));
		assertEquals("id", command.arrangeString("id", 2));
		assertEquals("id ", command.arrangeString("id", 3));
		assertEquals("id", command.arrangeString("id", -1));
	}

	@Test
	public void testEllipsisString() {
		assertEquals("usern...",
			command.ellipsisString("username", 6, "...", true));
		assertEquals("...rname",
			command.ellipsisString("username", 6, "...", false));

		assertEquals("user...",
			command.ellipsisString("username", 5, "...", true));
		assertEquals("...name",
			command.ellipsisString("username", 5, "...", false));

		assertEquals("Laser   ",
			command.ellipsisString("Laser", 8, "...", true));
		assertEquals("Laser   ",
			command.ellipsisString("Laser", 8, "...", false));

		assertEquals("Laser At...",
			command.ellipsisString("Laser Attack", 9, "...", true));
		assertEquals("...r Attack",
			command.ellipsisString("Laser Attack", 9, "...", false));

		assertEquals("...",
			command.ellipsisString("Laser Attack", 1, "...", true));
		assertEquals("...",
			command.ellipsisString("Laser Attack", 1, "...", false));

		assertEquals("Laser Attack",
			command.ellipsisString("Laser Attack", 0, "...", true));
		assertEquals("Laser Attack",
			command.ellipsisString("Laser Attack", 0, "...", false));

		assertEquals("Laser Attack",
			command.ellipsisString("Laser Attack", -1, "...", true));
		assertEquals("Laser Attack",
			command.ellipsisString("Laser Attack", -1, "...", false));
	}
}
