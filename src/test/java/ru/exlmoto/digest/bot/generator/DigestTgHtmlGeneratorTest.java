package ru.exlmoto.digest.bot.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.util.UpdateHelper;

@SpringBootTest
class DigestTgHtmlGeneratorTest {
	@Autowired
	private DigestTgHtmlGenerator htmlGenerator;

	private final UpdateHelper update = new UpdateHelper();

	@Test
	public void testGenerateDigestMessageHtmlReport() {
		System.out.println(htmlGenerator.generateDigestMessageHtmlReport(
			update.getSimpleMessage("#digest there is news!", "anyone"), "there is news!"));
	}
}
