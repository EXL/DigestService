package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class MetalRuMirrorParser extends MetalRuParser {
	@Override
	protected void commitAlt(String url, RateEntity entity, RateRepository repository, RestHelper rest) {
		if (parse(rest.getRestResponse(url).answer())) {
			commit(entity, repository);
		}
	}

	@Override
	protected void parseDocumentAux(Document document) {
		mirror = true;
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
