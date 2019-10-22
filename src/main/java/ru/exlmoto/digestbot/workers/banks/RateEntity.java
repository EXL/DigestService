package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;

import java.math.BigDecimal;

public abstract class RateEntity {
	protected String mPrevValue = null;
	protected BigDecimal mDifference = null;

	protected int mAddZeros  =  8;
	protected int mAddSpaces = 10;

	public boolean parseContent(final String aContent, final Logger aBotLogger) {
		clearValues();
		parseContentInner(aContent, aBotLogger);
		final boolean lIsAllOkay = checkValues();
		if (lIsAllOkay) {
			updateDifference(aBotLogger);
		}
		return lIsAllOkay;
	}

	public String generateAnswer() {
		if (!checkValues()) {
			return null;
		}
		return generateAnswerInner();
	}
	public abstract void parseContentInner(final String aContent, final Logger aBotLogger);
	public abstract boolean checkValues();
	public abstract String generateAnswerInner();
	public abstract void updateDifference(final Logger aBotLogger);
	public abstract void clearValues();

	public BigDecimal getDifference() {
		return mDifference;
	}

	protected String replaceCommasByDots(final String aStringWithCommas) {
		return aStringWithCommas.replaceAll(",", ".");
	}

	protected String addTrailingZeros(final String aValue) {
		return addTrailingSigns(aValue, '0', mAddZeros);
	}

	protected String addTrailingSpaces(final String aValue) {
		return addTrailingSigns(aValue, ' ', mAddSpaces);
	}

	private String addTrailingSigns(final String aValue, final Character aSign, final int aLimit) {
		if (aValue == null) {
			return null;
		}
		final StringBuilder lStringBuilder = new StringBuilder(aValue);
		final int lStart = aValue.length();
		for (int i = lStart; i < aLimit; i++) {
			lStringBuilder.append(aSign);
		}
		return lStringBuilder.toString();
	}

	protected String removeLastZeros(final String aValue) {
		if (aValue == null) {
			return null;
		}
		if (aValue.contains(".")) {
			final StringBuilder lStringBuilder = new StringBuilder(aValue);
			final int lStringLength = aValue.length() - 1;
			for (int i = lStringLength; i >= 0; --i) {
				if (aValue.charAt(i) == '0') {
					lStringBuilder.deleteCharAt(i);
				} else {
					break;
				}
			}
			return lStringBuilder.toString();
		}
		return aValue;
	}
}
