package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankBy extends Bank {
	private final String mUsdId = "145";
	private final String mEurId = "292";
	private final String mKztId = "301";
	private final String mRubId = "298";
	private final String mUahId = "290";
	private final String mGbpId = "143";

	@Override
	public void parseXml(final String aXml, final Logger aBotLogger) {
		mUSD = getCurrencyValue(aXml, mUsdId, aBotLogger);
		updateDifference(aBotLogger);
		mEUR = getCurrencyValue(aXml, mEurId, aBotLogger);
		mKZT = getCurrencyValue(aXml, mKztId, aBotLogger);
		mRUB = getCurrencyValue(aXml, mRubId, aBotLogger);
		mUAH = getCurrencyValue(aXml, mUahId, aBotLogger);
		mGBP = getCurrencyValue(aXml, mGbpId, aBotLogger);
	}

	private String getCurrencyValue(final String aXml, final String aCurrencyId, final Logger aBotLogger) {
		return normalizeValue(aXml,
			"/DailyExRates/Currency[@Id='" + aCurrencyId + "']/Scale/text()",
			"/DailyExRates/Currency[@Id='" + aCurrencyId + "']/Rate/text()",
			aBotLogger);
	}

	@Override
	public String generateMarkdownAnswer() {
		final boolean lAllOk = (mUSD != null) && (mEUR != null) && (mRUB != null) &&
			                       (mKZT != null) && (mUAH != null) && (mGBP != null);
		return (lAllOk) ? "```\n" +
			       "1 USD = " + mUSD + " BYN.\n" +
			       "1 EUR = " + mEUR + " BYN.\n" +
			       "1 KZT = " + mKZT + " BYN.\n" +
			       "1 RUB = " + mRUB + " BYN.\n" +
			       "1 UAH = " + mUAH + " BYN.\n" +
			       "1 GPB = " + mGBP + " BYN.\n" +
			       "```" : null;
	}
}
