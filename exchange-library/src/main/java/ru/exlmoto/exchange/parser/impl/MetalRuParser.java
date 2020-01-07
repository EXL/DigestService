package ru.exlmoto.exchange.parser.impl;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.exchange.parser.MetalParser;

import java.math.BigDecimal;

@Slf4j
public class MetalRuParser extends MetalParser {
	@Override
	protected void parseDocumentAux(Document document) {
		gold = parseValue(document, "1");
		silver = parseValue(document, "2");
		platinum = parseValue(document, "3");
		palladium = parseValue(document, "4");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.getElementsByClass("table").first();
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
		Element element = document.getElementsByClass("table").first();
		return element.select("tr").get(1).selectFirst("td").text();
	}

	@Override
	protected void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, Gold: %s, Silver: %s, Platinum: %s, Palladium: %s",
				date, gold, silver, platinum, palladium
			)
		);
	}
}
