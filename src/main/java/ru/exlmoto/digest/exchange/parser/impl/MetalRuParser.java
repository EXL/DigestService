package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.MetalParser;
import ru.exlmoto.digest.repository.ExchangeRateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class MetalRuParser extends MetalParser {
	@Override
	public void commitRates(String url, String mirror, ExchangeRateRepository repository, RestHelper rest) {
		ExchangeRateEntity metalRuEntity = repository.getMetalRu();
		if (parse(rest.getRestResponse(url).answer())) {
			commit(metalRuEntity, repository);
		} else {
			MetalRuMirrorParser metalRuMirrorParser = new MetalRuMirrorParser();
			if (metalRuMirrorParser.parse(rest.getRestResponse(mirror).answer())) {
				metalRuMirrorParser.commit(metalRuEntity, repository);
			}
		}
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.METAL_RU_ROW;
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
}
