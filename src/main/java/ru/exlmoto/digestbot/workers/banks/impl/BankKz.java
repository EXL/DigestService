package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankKz extends Bank {
	private final String mUsdId = "USD";
	private final String mEurId = "EUR";
	private final String mUahId = "UAH";
	private final String mBynId = "BYN";
	private final String mRubId = "RUB";
	private final String mGbpId = "GBP";

	private final String mTagId = "title";
	private final String mRateTag = "description";
	private final String mQuantTag = "quant";

	@Override
	public void parseXml(final String aXml, final Logger aBotLogger) {
		mUSD = getCurrencyValue(aXml, mUsdId, aBotLogger);
		updateDifference(aBotLogger);
		mEUR = getCurrencyValue(aXml, mEurId, aBotLogger);
		mRUB = getCurrencyValue(aXml, mRubId, aBotLogger);
		mBYN = getCurrencyValue(aXml, mBynId, aBotLogger);
		mUAH = getCurrencyValue(aXml, mUahId, aBotLogger);
		mGBP = getCurrencyValue(aXml, mGbpId, aBotLogger);
	}

	private String getCurrencyValue(final String aXml, final String aCurrencyId, final Logger aBotLogger) {
		return normalizeValueNode(aXml, "/rss/channel/item", mTagId, mRateTag, mQuantTag,
			aCurrencyId, aBotLogger);
	}

	@Override
	public String generateMarkdownAnswer() {
		final boolean lAllOk = (mUSD != null) && (mEUR != null) && (mRUB != null) &&
			                       (mBYN != null) && (mUAH != null) && (mGBP != null);
		return (lAllOk) ? "```\n" +
			       "1 USD = " + mUSD + " KZT.\n" +
			       "1 EUR = " + mEUR + " KZT.\n" +
			       "1 RUB = " + mRUB + " KZT.\n" +
			       "1 BYN = " + mBYN + " KZT.\n" +
			       "1 UAH = " + mUAH + " KZT.\n" +
			       "1 GPB = " + mGBP + " KZT.\n" +
			       "```" : null;
	}
}
