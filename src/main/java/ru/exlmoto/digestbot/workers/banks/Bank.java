package ru.exlmoto.digestbot.workers.banks;

public abstract class Bank {
	protected String mUSD;
	protected String mEUR;
	protected String mKZT;
	protected String mBYN;
	protected String mUAH;
	protected String mGBP;
	protected String mRUB;

	public abstract void parseXml(final String aXml);
}
