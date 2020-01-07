package ru.exlmoto.exchange.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Getter
public abstract class RateParser {
	protected String date = null;

	public boolean parse(String content) {
		try {
			processAux(content);
		} catch (Exception e) {
			log.error(String.format("Error while parsing document. Start: '%s'.", chopString(content)));
			return false;
		}
		return true;
	}

	private String chopString(String content) {
		int SMALL_STRING_SIZE = 40;
		if (content.length() < SMALL_STRING_SIZE) {
			return StringUtils.trimAllWhitespace(content);
		}
		return StringUtils.trimAllWhitespace(content.substring(0, SMALL_STRING_SIZE));
	}

	private void processAux(String content) {
		Assert.isTrue(StringUtils.isEmpty(content), "Processing: Received content is null/empty.");
		parseDocument(Jsoup.parse(content));
		logParsedValues();
	}

	protected BigDecimal parseValue(Document document, String valueId) {
		try {
			return parseValueAux(document, valueId);
		} catch (NumberFormatException nfe) {
			log.error("Error parsing some value from document.", nfe);
			return null;
		}
	}

	private void parseDocument(Document document) {
		Assert.notNull(document, "Document must not be null.");
		date = parseDate(document);
		parseDocumentAux(document);
	}

	protected abstract void parseDocumentAux(Document document);

	protected abstract BigDecimal parseValueAux(Document document, String valueId);

	protected abstract String parseDate(Document document);

	protected abstract void logParsedValues();

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected String filterSpaces(String value) {
		return value.replaceAll(" ", "");
	}
}
