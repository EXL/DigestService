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
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/show", "exlmoto"));

		doThrow(new InvalidDataAccessResourceUsageException("Test!"))
			.when(botDigestRepository).findAll(any(Pageable.class));

		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/show", "exlmoto"));
	}
}
