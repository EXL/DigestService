package ru.exlmoto.digest.bot.ability.message.impl;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.util.MessageHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.repository.BotDigestUserRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "bot.silent=true")
class DigestHashTagTest {
	@Autowired
	private DigestHashTag hashTag;

	@MockBean
	private BotDigestRepository botDigestRepository;

	@MockBean
	private BotDigestUserRepository botDigestUserRepository;

	@Autowired
	private BotHelper helper;

	@Autowired
	private BotSender sender;

	@Autowired
	private LocalizationHelper locale;

	@Test
	public void testDigestHashTag() {
		hashTag.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("#digest Test!", "anyone"));

		hashTag.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("#news Test!", "anyone"));

		hashTag.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("#digest  ", "anyone"));

		hashTag.execute(helper, sender, locale,
			new MessageHelper().getSimpleMessage("#news", "anyone"));
	}

	@Test
	public void testIsolateMessageText() {
		assertEquals("check", hashTag.isolateMessageText("#digest  check"));
		assertEquals("check", hashTag.isolateMessageText("#news  check"));

		assertEquals("", hashTag.isolateMessageText("#digest"));
		assertEquals("", hashTag.isolateMessageText("#news"));

		assertEquals("ddd check ad", hashTag.isolateMessageText("ddd  #digest  check <b>ad</b>  "));
		assertEquals("ddd check ad", hashTag.isolateMessageText("ddd  #news  check <b>ad</b>  "));
	}
}
