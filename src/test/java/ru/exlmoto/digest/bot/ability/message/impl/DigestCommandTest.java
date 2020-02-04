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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class DigestCommandTest {
	@Autowired
	private DigestCommand command;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	@Test
	public void testDigestCommand() {
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/digest", "anyone"));

		doThrow(new InvalidDataAccessResourceUsageException("Test!"))
			.when(botDigestRepository).findBotDigestEntitiesByChat(any(Pageable.class), anyLong());

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/digest", "anyone"));
	}
}
