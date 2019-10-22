package ru.exlmoto.digestbot.workers.banks;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import java.io.StringReader;
import java.math.BigDecimal;

public abstract class Bank extends RateEntity {
	protected String mUSD = null;
	protected String mEUR = null;
	protected String mKZT = null;
	protected String mBYN = null;
	protected String mUAH = null;
	protected String mGBP = null;
	protected String mRUB = null;

	private final DocumentBuilderFactory mDocumentBuilderFactory;
	private final XPath mXPath;

	public Bank() {
		mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		mXPath = XPathFactory.newInstance().newXPath();
	}

	public void parseXml(final String aXml, final Logger aBotLogger) {
		clearValues();
		parseXmlInner(aXml, aBotLogger);
	}
	public abstract void parseXmlInner(final String aXml, final Logger aBotLogger);

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

	private NodeList evaluateXPathNodes(final String aAllXml, final String aXPathExpression, final Logger aBotLogger) {
		try {
			final DocumentBuilder lDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
			final Document lDocument = lDocumentBuilder.parse(new InputSource(new StringReader(aAllXml)));
			final XPathExpression expr = mXPath.compile(aXPathExpression);
			return (NodeList) expr.evaluate(lDocument, XPathConstants.NODESET);
		} catch (Exception e) {
			aBotLogger.error(String.format("Cannot parse XML or XPath: '%s'.", e.toString()));
		}
		return null;
	}

	@Override
	public void updateDifference(final Logger aBotLogger) {
		if (mUSD != null) {
			if (mPrevValue != null) {
				try {
					final BigDecimal lNewValue = new BigDecimal(mPrevValue).subtract(new BigDecimal(mUSD));
					if (lNewValue.compareTo(BigDecimal.ZERO) == 0) {
						mDifference = null;
					} else {
						mDifference = lNewValue;
					}
				} catch (Exception e) {
					mDifference = null;
					aBotLogger.error(String.format("Cannot update bank difference: '%s'.", e.toString()));
				}
			} else {
				mDifference = null;
				mPrevValue = mUSD;
			}
		} else {
			mDifference = null;
		}
	}

	protected String normalizeValue(final String aXml,
	                                final String aXPathNominal,
	                                final String aXPathValue,
	                                final Logger aBotLogger) {
		try {
			final String lNominal = evaluateXPath(aXml, aXPathNominal, aBotLogger);
			final String lValue = evaluateXPath(aXml, aXPathValue, aBotLogger);
			if (lNominal != null && lValue != null) {
				final BigDecimal lNominalBigDecimal = new BigDecimal(replaceCommasByDots(lNominal));
				final BigDecimal lValueBigDecimal = new BigDecimal(replaceCommasByDots(lValue));
				return addTrailingZeros(String.format("%.4f", lValueBigDecimal.divide(lNominalBigDecimal,
					BigDecimal.ROUND_FLOOR)));
			}
		} catch (Exception e) {
			aBotLogger.error(String.format("Cannot parse BigDecimal value from XML: '%s'.", e.toString()));
		}
		return null;
	}

	protected String normalizeValueNode(final String aXml,
	                                    final String aXPathObjects,
	                                    final String aIdTag,
	                                    final String aRateTag,
	                                    final String aQuantTag,
	                                    final String aRateId,
	                                    final Logger aBotLogger) {
		try {
			final NodeList lNodeList = evaluateXPathNodes(aXml, aXPathObjects, aBotLogger);
			int lLength = 0;
			if (lNodeList != null) {
				lLength = lNodeList.getLength();
			}
			for (int i = 0; i < lLength; i++) {
				if (lNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element lElement = (Element) lNodeList.item(i);
					if (lElement.getElementsByTagName(aIdTag).item(0).getTextContent().equals(aRateId)) {
						if (aQuantTag != null) {
							final String lValue = lElement.getElementsByTagName(aRateTag).item(0).getTextContent();
							final String lQuant = lElement.getElementsByTagName(aQuantTag).item(0).getTextContent();
							final BigDecimal lValueBigDecimal = new BigDecimal(replaceCommasByDots(lValue));
							final BigDecimal lQuantBigDecimal = new BigDecimal(replaceCommasByDots(lQuant));
							return addTrailingZeros(String.format("%.4f", lValueBigDecimal.divide(lQuantBigDecimal,
								BigDecimal.ROUND_FLOOR)));
						} else {
							final String lValue = lElement.getElementsByTagName(aRateTag).item(0).getTextContent();
							final BigDecimal lValueBigDecimal = new BigDecimal(replaceCommasByDots(lValue));
							return addTrailingZeros(String.format("%.4f", lValueBigDecimal));
						}
					}
				}
			}
		} catch (Exception e) {
			aBotLogger.error(String.format("Cannot parse BigDecimal value from XML: '%s'.", e.toString()));
		}
		return null;
	}
}
