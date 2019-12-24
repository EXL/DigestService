package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.exchange.rate.BankEntity;

import java.math.BigDecimal;

public class BankKz extends BankEntity {
	private final Logger LOG = LoggerFactory.getLogger(BankKz.class);

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "USD");
		eur = parseValue(document, "EUR");
		rub = parseValue(document, "RUB");
		byn = parseValue(document, "BYN");
		uah = parseValue(document, "UAH");
		gbp = parseValue(document, "GBP");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("title:contains(" + valueId + ")").parent();
		BigDecimal quant = new BigDecimal(filterCommas(element.selectFirst("quant").text()));;
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("description").text()));
		return  value.divide(quant, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("title:contains(USD)").parent();
		return element.selectFirst("pubDate").text();
	}

	@Override
	protected boolean testParsedValues() {
		return date != null && usd != null && eur != null && kzt != null && byn != null && uah != null && gbp != null;
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, USD: %s, EUR: %s, RUB: %s, BYN: %s, UAH: %s, GBP: %s",
				date, usd, eur, rub, byn, uah, gbp
			)
		);
	}
}
