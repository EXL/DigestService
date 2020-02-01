package ru.exlmoto.digest.bot.generator;

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
		System.out.println(htmlGenerator.generateDigestMessageHtmlReport(
			new MessageHelper().getSimpleMessage("#digest there is news!", "anyone"),
			"there is news!"));
	}
}
