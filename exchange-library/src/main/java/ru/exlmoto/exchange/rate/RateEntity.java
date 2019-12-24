package ru.exlmoto.exchange.rate;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
public abstract class RateEntity {
	private final String url;

	public RateEntity(String url) {
		this.url = url;
		try {
			parseDocument(getDocument());
		} catch (IOException e) {
			log.error("");
		}
	}

	private Document getDocument() throws IOException {
		// TODO: Seconds
		return Jsoup.connect(url).timeout(10 * 1000).get();
	}

	protected String filterCommas(String value) {
		return value.replaceAll(",", ".");
	}

	protected abstract void parseDocument(Document document);
}
