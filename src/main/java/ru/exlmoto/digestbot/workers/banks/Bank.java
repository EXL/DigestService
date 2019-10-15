package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import java.io.StringReader;

public abstract class Bank {
	protected String mUSD = null;
	protected String mEUR = null;
	protected String mKZT = null;
	protected String mBYN = null;
	protected String mUAH = null;
	protected String mGBP = null;
	protected String mRUB = null;

	private String mPrevUSD = null;
	private Double mDifference = null;

	private final DocumentBuilderFactory mDocumentBuilderFactory;
	private final XPath mXPath;

	public Bank() {
		mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		mXPath = XPathFactory.newInstance().newXPath();
	}

	public abstract void parseXml(final String aXml, final Logger aBotLogger);

	private String evaluateXPath(final String aAllXml, final String aXPathExpression, final Logger aBotLogger) {
		try {
			final DocumentBuilder lDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
			final Document lDocument = lDocumentBuilder.parse(new InputSource(new StringReader(aAllXml)));
			final XPathExpression expr = mXPath.compile(aXPathExpression);
			return (String) expr.evaluate(lDocument, XPathConstants.STRING);
		} catch (Exception e) {
			aBotLogger.error(String.format("Cannot parse XML or XPath: '%s'.", e.toString()));
		}
		return null;
	}

	private String addTrailingZeros(final String aValue) {
		final StringBuilder lStringBuilder = new StringBuilder(aValue);
		for (int i = aValue.indexOf('.'); i < 3; i++) {
			lStringBuilder.append('0');
		}
		return lStringBuilder.toString();
	}

	protected void updateDifference(final Logger aBotLogger) {
		if (mUSD != null) {
			if (mPrevUSD != null) {
				try {
					final Double lNewValue = Double.parseDouble(mPrevUSD) - Double.parseDouble(mUSD);
					if (Math.abs(lNewValue) < 2 * Double.MIN_VALUE) {
						mDifference = null;
					} else {
						mDifference = lNewValue;
					}
				} catch (Exception e) {
					mDifference = null;
					aBotLogger.error(String.format("Cannot update difference: '%s'.", e.toString()));
				}
			} else {
				mDifference = null;
				mPrevUSD = mUSD;
			}
		} else {
			mDifference = null;
		}
	}

	public Double getDifference() {
		return mDifference;
	}

	protected String normalizeValue(final String aXml,
	                                final String aXPathNominal,
	                                final String aXPathValue,
	                                final Logger aBotLogger) {
		final String lNominal = evaluateXPath(aXml, aXPathNominal, aBotLogger);
		final String lValue = evaluateXPath(aXml, aXPathValue, aBotLogger);
		if (lNominal != null && lValue != null) {
			try {
				final Double lNominalDouble = Double.valueOf(lNominal.replaceAll(",", "."));
				final Double lValueDouble = Double.valueOf(lValue.replaceAll(",", "."));
				return addTrailingZeros(String.format("%.4f", lValueDouble / lNominalDouble));
			} catch (Exception e) {
				aBotLogger.error(String.format("Cannot parse double value from XML: '%s'.", e.toString()));
			}
		}
		return null;
	}
}
