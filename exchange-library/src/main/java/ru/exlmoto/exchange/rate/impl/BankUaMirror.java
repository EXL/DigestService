package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.exchange.rate.BankEntity;

import java.math.BigDecimal;

public class BankUaMirror extends BankEntity {
	private final Logger LOG = LoggerFactory.getLogger(BankUaMirror.class);

	@Override
	protected void parseDocument(Document document) {
		date = parseDate(document);
		usd = parseValue(document, "6");
		eur = parseValue(document, "8");
		kzt = parseValue(document, "10");
		byn = parseValue(document, "48");
		rub = parseValue(document, "17");
		gbp = parseValue(document, "3");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Valute[ID=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Nominal").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Value").text()));
		return value.divide(nominal, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("ValCurs").attr("Date");
	}

	@Override
	protected boolean testParsedValues() {
		return date != null && usd != null && eur != null && kzt != null && byn != null && rub != null && gbp != null;
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, GBP: %s, DIFF: %s",
				date, usd, eur, kzt, byn, rub, gbp, prev
			)
		);
	}
}
