package ru.exlmoto.exchange.parser.impl;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.exchange.parser.BankParser;

import java.math.BigDecimal;

@Slf4j
public class BankByParser extends BankParser {
	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "145");
		eur = parseValue(document, "292");
		kzt = parseValue(document, "301");
		rub = parseValue(document, "298");
		uah = parseValue(document, "290");
		gbp = parseValue(document, "143");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Currency[Id=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Scale").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Rate").text()));
		return value.divide(nominal, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("DailyExRates").attr("Date");
	}

	@Override
	protected boolean checkParsedValues() {
		return usd != null || eur != null || kzt != null || rub != null || uah != null || gbp != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, RUB: %s, UAH: %s, GBP: %s",
				date, usd, eur, kzt, rub, uah, gbp
			)
		);
	}
}
