package ru.exlmoto.digestbot.workers.banks.impl;

import org.slf4j.Logger;

import ru.exlmoto.digestbot.workers.banks.Bank;

public class BankUa extends Bank {
	private final String mUsdIdM = "6";
	private final String mEurIdM = "8";
	private final String mKztIdM = "10";
	private final String mBynIdM = "48";
	private final String mRubIdM = "17";
	private final String mGbpIdM = "3";

	private final String mUsdId = "840";
	private final String mEurId = "978";
	private final String mKztId = "398";
	private final String mBynId = "933";
	private final String mRubId = "643";
	private final String mGbpId = "826";

	private final String mTagId = "r030";
	private final String mRateTag = "rate";

	private boolean mIsMirror = false;

	@Override
	public void parseXml(final String aXml, final Logger aBotLogger) {
		mUSD = getCurrencyValue(aXml, (mIsMirror) ? mUsdIdM : mUsdId, aBotLogger);
		updateDifference(aBotLogger);
		mEUR = getCurrencyValue(aXml, (mIsMirror) ? mEurIdM : mEurId, aBotLogger);
		mKZT = getCurrencyValue(aXml, (mIsMirror) ? mKztIdM : mKztId, aBotLogger);
		mBYN = getCurrencyValue(aXml, (mIsMirror) ? mBynIdM : mBynId, aBotLogger);
		mRUB = getCurrencyValue(aXml, (mIsMirror) ? mRubIdM : mRubId, aBotLogger);
		mGBP = getCurrencyValue(aXml, (mIsMirror) ? mGbpIdM : mGbpId, aBotLogger);
	}

	private String getCurrencyValue(final String aXml, final String aCurrencyId, final Logger aBotLogger) {
		return (mIsMirror) ?
			normalizeValue(aXml, "/ValCurs/Valute[@id='" + aCurrencyId + "']/Nominal/text()",
				"/ValCurs/Valute[@id='" + aCurrencyId + "']/Value/text()", aBotLogger) :
			normalizeValueNode(aXml, "/exchange/currency", mTagId, mRateTag, null,
				aCurrencyId, aBotLogger);
	}

	@Override
	public String generateMarkdownAnswer() {
		return "```\n" +
			       "1 USD = " + mUSD + " UAH.\n" +
			       "1 EUR = " + mEUR + " UAH.\n" +
			       "1 KZT = " + mKZT + " UAH.\n" +
			       "1 BYN = " + mBYN + " UAH.\n" +
			       "1 RUB = " + mRUB + " UAH.\n" +
			       "1 GPB = " + mGBP + " UAH.\n" +
			       "```";
	}

	public void setIsMirror(final boolean aIsMirror) {
		mIsMirror = aIsMirror;
	}
}
