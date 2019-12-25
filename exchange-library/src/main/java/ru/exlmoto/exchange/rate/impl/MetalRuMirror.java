package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.repository.MetalRuRepository;

import java.math.BigDecimal;

@Component
public class MetalRuMirror extends MetalRu {
	public MetalRuMirror(MetalRuRepository repository) {
		super(repository);
	}

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
}
