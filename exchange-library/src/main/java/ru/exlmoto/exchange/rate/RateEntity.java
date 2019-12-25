package ru.exlmoto.exchange.rate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;

public abstract class RateEntity {
	private final Logger LOG = LoggerFactory.getLogger(RateEntity.class);
	private final int TIMEOUT = 15;

	protected String date = null;

	public boolean process(String url) {
		try {
			return processAux(url, false);
		} catch (Exception e) {
			LOG.error("Error while connect or parsing document. Jsoup connection. Attempt #1.", e);
			try {
				return processAux(url, true);
			} catch (Exception ex) {
				LOG.error("Error while connect or parsing document. Spring RestTemplate connection. Attempt #2.", ex);
				return false;
			}
		}
	}

	private boolean processAux(String url, boolean spring) throws IOException {
		parseDocument((spring) ? getDocumentSpring(url) : getDocumentSoup(url));
		logParsedValues();
		return testParsedValues();
	}

	private Document getDocumentSoup(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIMEOUT * 1000).get();
	}

	private Document getDocumentSpring(String url) {
		RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(TIMEOUT)).build();
		String rawData = restTemplate.getForObject(url, String.class);
		Assert.notNull(rawData, "Received raw data is null");
		return Jsoup.parse(rawData);
	}

	protected BigDecimal parseValue(Document document, String valueId) {
		try {
			return parseValueAux(document, valueId);
		} catch (NumberFormatException nfe) {
			LOG.error("Error parsing some value from document.", nfe);
			return null;
		}
	}

	private void parseDocument(Document document) {
		Assert.notNull(document,"Document must not be null.");
		date = parseDate(document);
		parseDocumentAux(document);
	}

	protected abstract void parseDocumentAux(Document document);
	protected abstract BigDecimal parseValueAux(Document document, String valueId);
	protected abstract String parseDate(Document document);
	protected abstract boolean testParsedValues();
	protected abstract void logParsedValues();

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected String filterSpaces(String value) {
		return value.replaceAll(" ", "");
	}

	public String getDate() {
		return date;
	}
}
