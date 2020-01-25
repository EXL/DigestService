package ru.exlmoto.digest.exchange.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public abstract class RateParser {
	private final Logger log = LoggerFactory.getLogger(RateParser.class);

	protected String date = null;
	protected boolean mirror = false;

	public boolean parse(String content) {
		if (!StringUtils.isEmpty(content)) {
			try {
				return parseDocument(Jsoup.parse(content));
			} catch (Exception e) {
				log.error(String.format("Error while parsing document. Chunk: '%s'.", chopContent(content)), e);
				/*
				 * Uncomment the line below to be able to get values from part of the document. It may be dangerous
				 * because some services give you a chunk of data, while on the mirror you can get all the values.
				 */
				// return checkParsedValues();
			}
		}
		return false;
	}

	private String chopContent(String content) {
		int SMALL_STRING_SIZE = 50;
		if (content.length() < SMALL_STRING_SIZE) {
			return filterLines(content);
		}
		return filterLines(content.substring(0, SMALL_STRING_SIZE));
	}

	protected BigDecimal parseValue(Document document, String valueId) {
		try {
			return parseValueAux(document, valueId);
		} catch (NumberFormatException nfe) {
			log.error("Error parsing some value from document.", nfe);
			return null;
		}
	}

	private boolean parseDocument(Document document) {
		Assert.notNull(document, "Document must not be null.");
		date = parseDate(document);
		parseDocumentAux(document);
		return checkParsedValues();
	}

	protected abstract void parseDocumentAux(Document document);

	protected abstract boolean checkParsedValues();

	protected abstract BigDecimal parseValueAux(Document document, String valueId);

	protected abstract String parseDate(Document document);

	public abstract void logParsedValues();

	private String filterLines(String value) {
		return value.replaceAll("[\\t\\n\\r]+"," ").trim();
	}

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected String filterSpaces(String value) {
		return value.replaceAll(" ", "");
	}

	public String getDate() {
		return date;
	}

	public boolean isMirror() {
		return mirror;
	}

	public void setMirror(boolean mirror) {
		this.mirror = mirror;
	}
}
