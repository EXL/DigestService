package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;

import java.math.BigDecimal;

public abstract class Metal extends RateEntity {
	protected String mGold = null;
	protected String mSilver = null;
	protected String mPlatinum = null;
	protected String mPalladium = null;

	public boolean parseHtml(final String aHtml, final Logger aBotLogger) {
		return parseContent(aHtml, aBotLogger);
	}

	public abstract void parseContentInner(final String aHtml, final Logger aBotLogger);

	@Override
	public void updateDifference(Logger aBotLogger) {
		if (mGold != null) {
			if (mPrevValue != null) {
				try {
					final BigDecimal lNewValue = new BigDecimal(mPrevValue).subtract(new BigDecimal(mGold));
					if (lNewValue.compareTo(BigDecimal.ZERO) == 0) {
						mDifference = null;
					} else {
						mDifference = lNewValue;
					}
				} catch (Exception e) {
					mDifference = null;
					aBotLogger.error(String.format("Cannot update metal difference: '%s'.", e.toString()));
				}
			} else {
				mDifference = null;
				mPrevValue = mGold;
			}
		} else {
			mDifference = null;
		}
	}
}
