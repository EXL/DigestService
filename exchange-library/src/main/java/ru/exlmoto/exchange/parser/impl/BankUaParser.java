package ru.exlmoto.exchange.parser.impl;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.exchange.parser.BankParser;

import java.math.BigDecimal;

@Slf4j
public class BankUaParser extends BankParser {
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
	protected boolean checkParsedValues() {
		return usd != null || eur != null || kzt != null || byn != null || rub != null || gbp != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, gbp
			)
		);
	}
}