package ru.exlmoto.digestbot.workers.banks;

public abstract class Metal {
	protected String mGold;
	protected String mSilver;
	protected String mPlatina;
	protected String mPalladium;

	public abstract void parseHtml(final String aHtml);
}
