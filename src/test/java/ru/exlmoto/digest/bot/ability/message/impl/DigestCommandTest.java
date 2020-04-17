package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.UpdateHelper;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class DigestCommandTest {
	@Autowired
	private DigestCommand command;

	@MockBean
	private DatabaseService service;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testDigestCommand() {
		command.execute(helper, sender, locale, update.getSimpleMessage("/digest", "anyone"));

		doThrow(new InvalidDataAccessResourceUsageException("Test!"))
			.when(service).getChatDigestsCommand(anyInt(), anyInt(), anyLong());

		command.execute(helper, sender, locale, update.getSimpleMessage("/digest", "anyone"));
	}
}
