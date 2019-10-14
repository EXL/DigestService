package ru.exlmoto.digestbot.workers.banks.impl;

import ru.exlmoto.digestbot.workers.banks.Metal;

public class MetalRu extends Metal {
	@Override
	public void parseHtml(String aHtml) {
		System.out.println(aHtml);
	}
}
