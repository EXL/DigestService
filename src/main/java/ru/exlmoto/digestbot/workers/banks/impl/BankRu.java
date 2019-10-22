package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankRu extends Bank {
	private final String mUsdId = "R01235";
	private final String mEurId = "R01239";
	private final String mKztId = "R01335";
	private final String mBynId = "R01090B";
	private final String mUahId = "R01720";
	private final String mGbpId = "R01035";

	@Override
	public void parseXmlInner(final String aXml, final Logger aBotLogger) {
		mUSD = getCurrencyValue(aXml, mUsdId, aBotLogger);
		updateDifference(aBotLogger);
		mEUR = getCurrencyValue(aXml, mEurId, aBotLogger);
		mKZT = getCurrencyValue(aXml, mKztId, aBotLogger);
		mBYN = getCurrencyValue(aXml, mBynId, aBotLogger);
		mUAH = getCurrencyValue(aXml, mUahId, aBotLogger);
		mGBP = getCurrencyValue(aXml, mGbpId, aBotLogger);
	}

	private String getCurrencyValue(final String aXml, final String aCurrencyId, final Logger aBotLogger) {
		return normalizeValue(aXml,
			"/ValCurs/Valute[@ID='" + aCurrencyId + "']/Nominal/text()",
			"/ValCurs/Valute[@ID='" + aCurrencyId + "']/Value/text()",
			aBotLogger);
	}

	@Override
	public String generateAnswerInner() {
		return "```\n" +
			       "1 USD = " + mUSD + " RUB.\n" +
			       "1 EUR = " + mEUR + " RUB.\n" +
			       "1 KZT = " + mKZT + " RUB.\n" +
			       "1 BYN = " + mBYN + " RUB.\n" +
			       "1 UAH = " + mUAH + " RUB.\n" +
			       "1 GPB = " + mGBP + " RUB.\n" +
				   "```";
	}

	@Override
	public boolean checkValues() {
		return (mUSD != null) && (mEUR != null) && (mKZT != null) && (mBYN != null) && (mUAH != null) && (mGBP != null);
	}

	@Override
	public void clearValues() {
		mUSD = null;
		mEUR = null;
		mKZT = null;
		mBYN = null;
		mUAH = null;
		mGBP = null;
	}
}
