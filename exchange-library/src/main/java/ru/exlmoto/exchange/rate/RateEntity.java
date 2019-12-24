package ru.exlmoto.exchange.rate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;

public abstract class RateEntity {
	private final Logger LOG = LoggerFactory.getLogger(RateEntity.class);
	private final int TIMEOUT = 10;

	protected BigDecimal prev = null;
	protected String date = null;

	public boolean process(String url, boolean useSpringBootRestTemplate) {
		try {
			parseDocument((useSpringBootRestTemplate) ? getDocumentRest(url) : getDocument(url));
			logParsedValues();
			return testParsedValues();
		} catch (Exception e) {
			prev = null;
			LOG.error(String.format("Error while parsing document: '%s'", e.toString()));
			return false;
		}
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIMEOUT * 1000).get();
	}

	private Document getDocumentRest(String url) throws IOException {
		RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(TIMEOUT)).build();
		String rawData = restTemplate.getForObject(url, String.class);
		if (rawData != null) {
			return Jsoup.parse(rawData);
		} else {
			throw new IOException("Received raw data is null.");
		}
	}

	protected BigDecimal parseValue(Document document, String valueId) {
		try {
			return parseValueAux(document, valueId);
		} catch (NumberFormatException nfe) {
			LOG.error(String.format("Error parsing some value from document: '%s'", nfe.toString()));
			return null;
		}
	}

	protected abstract void parseDocument(Document document);
	protected abstract void logParsedValues();
	protected abstract BigDecimal parseValueAux(Document document, String valueId);
	protected abstract String parseDate(Document document);
	protected abstract boolean testParsedValues();

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}
}
