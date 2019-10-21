package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;

import java.math.BigDecimal;

public abstract class RateEntity {
	protected String mPrevValue = null;
	protected BigDecimal mDifference = null;

	public abstract String generateMarkdownAnswer();
	public abstract void updateDifference(final Logger aBotLogger);

	public BigDecimal getDifference() {
		return mDifference;
	}
}
