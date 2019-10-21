package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;
import ru.exlmoto.digestbot.workers.banks.Metal;

public class MetalRu extends Metal {
	@Override
	public void parseHtml(final String aHtml, final Logger aBotLogger) {
		// System.out.println(aHtml);
	}

	@Override
	public String generateMarkdownAnswer() {
		return null;
	}
}
