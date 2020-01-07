package ru.exlmoto.exchange.parser;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Getter
@Setter
public abstract class RateParser {
	@Setter(AccessLevel.NONE)
	protected String date = null;
	protected boolean mirror = false;

	public boolean parse(String content) {
		if (!StringUtils.isEmpty(content)) {
			try {
				return parseDocument(Jsoup.parse(content));
			} catch (Exception e) {
				log.error(String.format("Error while parsing document. Chunk: '%s'.", chopContent(content)), e);
				return false;
			}
		}
		return false;
	}

	private String chopContent(String content) {
		int SMALL_STRING_SIZE = 40;
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
		return true;
	}

	protected abstract void parseDocumentAux(Document document);

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
}
