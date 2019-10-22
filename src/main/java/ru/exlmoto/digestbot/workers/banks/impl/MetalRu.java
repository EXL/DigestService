package ru.exlmoto.digestbot.workers.banks.impl;

import org.jsoup.Jsoup;

import org.slf4j.Logger;

import org.springframework.util.StringUtils;

import ru.exlmoto.digestbot.workers.banks.Metal;
import ru.exlmoto.digestbot.yaml.impl.YamlRatesIndexHelper;

public class MetalRu extends Metal {
	private String mDate = null;

	private final String mMarker = "table class=\"data\"";

	private final int mDateLine      = 9;
	private final int mGoldLine      = 10;
	private final int mSilverLine    = 11;
	private final int mPlatinumLine  = 12;
	private final int mPalladiumLine = 13;
	private final int mLineCount     = 15;

	private final YamlRatesIndexHelper mYamlRatesIndexHelper;

	public MetalRu(final YamlRatesIndexHelper aYamlRatesIndexHelper) {
		mYamlRatesIndexHelper = aYamlRatesIndexHelper;
		mAddZeros = 7;
	}

	@Override
	public void parseContentInner(final String aHtml, final Logger aBotLogger) {
		final String lHtmlChunk = getHtmlChunk(aHtml, aHtml.indexOf(mMarker), mLineCount);
		mDate = getClearTextFromHtml(getHtmlLine(lHtmlChunk, mDateLine));
		mGold = addTrailingZeros(removeLastZeros(getClearTextFromHtml(getHtmlLine(lHtmlChunk, mGoldLine))));
		mSilver = addTrailingZeros(removeLastZeros(getClearTextFromHtml(getHtmlLine(lHtmlChunk, mSilverLine))));
		mPlatinum = addTrailingZeros(removeLastZeros(getClearTextFromHtml(getHtmlLine(lHtmlChunk, mPlatinumLine))));
		mPalladium = addTrailingZeros(removeLastZeros(getClearTextFromHtml(getHtmlLine(lHtmlChunk, mPalladiumLine))));
	}

	private String getClearTextFromHtml(final String aHtmlLine) {
		return (aHtmlLine != null) ?
			replaceCommasByDots(StringUtils.trimAllWhitespace(Jsoup.parse(aHtmlLine).text())) : null;
	}

	private String getHtmlLine(final String aHtmlChunk, final int aLine) {
		if (aHtmlChunk == null) {
			return null;
		}
		final int lTextSize = aHtmlChunk.length();
		final StringBuilder lStringBuilder = new StringBuilder();
		for (int i = 0, j = 0; i < lTextSize && j < aLine; i++) {
			final char lChar = aHtmlChunk.charAt(i);
			if (j == aLine - 1) {
				lStringBuilder.append(lChar);
			}
			if (lChar == '\n') {
				++j;
			}
		}
		return lStringBuilder.toString().trim();
	}

	private String getHtmlChunk(final String aHtml, final int aStart, final int aLineCount) {
		if (aStart == -1) {
			return null;
		}
		final int lTextSize = aHtml.length();
		final StringBuilder lStringBuilder = new StringBuilder();
		for (int i = aStart, j = 0; i < lTextSize && j < aLineCount; i++) {
			final char lChar = aHtml.charAt(i);
			// Skip first chunk line.
			if (j > 0) {
				lStringBuilder.append(lChar);
			}
			if (lChar == '\n') {
				++j;
			}
		}
		return lStringBuilder.toString();
	}

	@Override
	public String generateAnswerInner() {
		final String lRoubles = mYamlRatesIndexHelper.getTitleByKey("rate.metal.roubles");
		final String lGold = addTrailingSpaces(mYamlRatesIndexHelper.getTitleByKey("rate.metal.gold"));
		final String lSilver = addTrailingSpaces(mYamlRatesIndexHelper.getTitleByKey("rate.metal.silver"));
		final String lPlatinum = addTrailingSpaces(mYamlRatesIndexHelper.getTitleByKey("rate.metal.platinum"));
		final String lPalladium = addTrailingSpaces(mYamlRatesIndexHelper.getTitleByKey("rate.metal.palladium"));
		String lAnswer = mYamlRatesIndexHelper.getTitleByKey("rate.metal.prices") + ' ';
		lAnswer += mDate + ":\n```\n";
		lAnswer += lGold + ' ' + mGold + ' ' + lRoubles + '\n';
		lAnswer += lSilver + ' ' + mSilver + ' ' + lRoubles + '\n';
		lAnswer += lPlatinum + ' ' + mPlatinum + ' ' + lRoubles + '\n';
		lAnswer += lPalladium + ' ' + mPalladium + ' ' + lRoubles + "\n```";
		return lAnswer;
	}

	@Override
	public boolean checkValues() {
		return (mDate != null) && (mGold != null) && (mSilver != null) && (mPlatinum != null) && (mPalladium != null);
	}

	@Override
	public void clearValues() {
		mDate = null;
		mGold = null;
		mSilver = null;
		mPlatinum = null;
		mPalladium = null;
	}
}
