package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;

public abstract class Metal extends RateEntity {
	protected String mGold;
	protected String mSilver;
	protected String mPlatina;
	protected String mPalladium;

	public abstract void parseHtml(final String aHtml, final Logger aBotLogger);

	@Override
	public void updateDifference(Logger aBotLogger) {

	}
}
