package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.exchange.rate.Metal;

import java.math.BigDecimal;

public class MetalRuMirror extends Metal {
	private final Logger LOG = LoggerFactory.getLogger(MetalRuMirror.class);

	@Override
	protected void parseDocumentAux(Document document) {
		gold = parseValue(document, "1");
		silver = parseValue(document, "2");
		platinum = parseValue(document, "3");
		palladium = parseValue(document, "4");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.getElementsByClass("mfd-table").first();
		return new BigDecimal(
			filterCommas(
				filterSpaces(
					element.select("tr").get(1).select("td").get(Integer.parseInt(valueId)).text()
				)
			)
		);
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.getElementsByClass("mfd-table").first();
		return element.getElementsByClass("mfd-item-date").first().text();
	}

	@Override
	protected boolean testParsedValues() {
		return date != null && gold != null && silver != null && platinum != null && palladium != null;
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, Gold: %s, Silver: %s, Platinum: %s, Palladium: %s",
				date, gold, silver, platinum, palladium
			)
		);
	}
}
