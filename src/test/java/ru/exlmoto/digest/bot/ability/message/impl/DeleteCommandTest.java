package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(properties = "bot.silent=true")
class DeleteCommandTest {
	@Autowired
	private DeleteCommand command;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocaleHelper locale;

	@Test
	public void testDeleteCommand() {
		/* Format error. */
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete", "anyone"));
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete asd sad", "anyone"));
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete 5 sad", "anyone"));
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete 5 4", "anyone"));
		System.out.println("===");

		/* Wrong id. */
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete asd", "anyone"));
		System.out.println("===");

		/* Ok. */
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete 5", "anyone"));
		System.out.println("===");

		/* Exception. */
		doThrow(new InvalidDataAccessResourceUsageException("Test!")).when(botDigestRepository).deleteById(anyLong());
		command.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("/delete 42", "anyone"));
		System.out.println("===");
	}
}
