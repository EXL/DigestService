package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankUa extends Bank {
	@Override
	public void parseXml(final String aXml, final Logger aBotLogger) {
		// System.out.println(aXml);
	}

	@Override
	public String generateMarkdownAnswer() {
		return null;
	}
}
