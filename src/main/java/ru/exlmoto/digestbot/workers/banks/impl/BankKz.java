package ru.exlmoto.digestbot.workers.banks.impl;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankKz extends Bank {
	@Override
	public void parseXml(String aXml) {
		System.out.println(aXml);
	}
}
