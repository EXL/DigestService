package ru.exlmoto.digest.bot.generator;

import com.pengrad.telegrambot.model.Message;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.MessageHelper;

@SpringBootTest
class DigestTgHtmlGeneratorTest {
	@Autowired
	private DigestTgHtmlGenerator htmlGenerator;

	@Test
	public void testGenerateDigestMessageHtmlReport() {
		Message message = new MessageHelper().getSimpleMessage("#digest there is news!", "anyone");
		System.out.println(
			htmlGenerator.generateDigestMessageHtmlReport(message.chat(), message.from(),
				message.messageId(), message.date(), "there is news!")
		);
	}
}
