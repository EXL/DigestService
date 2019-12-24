package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.exchange.rate.BankEntity;

import java.math.BigDecimal;

public class BankUa extends BankEntity {
	private final Logger LOG = LoggerFactory.getLogger(BankUa.class);

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "840");
		eur = parseValue(document, "978");
		kzt = parseValue(document, "398");
		byn = parseValue(document, "933");
		rub = parseValue(document, "643");
		gbp = parseValue(document, "826");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("r030:contains(" + valueId + ")").parent();
		return new BigDecimal(filterCommas(element.selectFirst("rate").text()));
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("r030:contains(840)").parent();
		return element.selectFirst("exchangedate").text();
	}

	@Override
	protected boolean testParsedValues() {
		return date != null && usd != null && eur != null && kzt != null && byn != null && rub != null && gbp != null;
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, gbp
			)
		);
	}
}
