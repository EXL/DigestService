package ru.exlmoto.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;

public class BankUaMirrorParser extends BankUaParser {
	@Override
	protected void parseDocumentAux(Document document) {
		mirror = true;
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
}
