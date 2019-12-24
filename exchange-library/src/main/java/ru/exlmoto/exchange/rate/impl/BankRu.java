package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.exchange.rate.BankEntity;

import java.math.BigDecimal;

public class BankRu extends BankEntity {
	public BankRu(String url) {
		super(url);
	}

	@Override
	protected void parseDocument(Document document) {
		usd = parseValue(document, "R01235");
		eur = parseValue(document, "R01239");
		kzt = parseValue(document, "R01335");
		byn = parseValue(document, "R01090B");
		uah = parseValue(document, "R01720");
		gbp = parseValue(document, "R01035");
	}

	private BigDecimal parseValue(Document document, String valueId) {
		try {
			Element element = document.selectFirst("Valute[ID=" + valueId + "]");
			BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Nominal").text()));
			BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Value").text()));
			return value.divide(nominal, BigDecimal.ROUND_FLOOR);
		} catch (Exception exception) {
			return null;
		}
	}
}
