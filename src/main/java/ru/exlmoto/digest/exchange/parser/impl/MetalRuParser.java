package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.MetalParser;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class MetalRuParser extends MetalParser {
	@Override
	public void commitRates(String url, String mirror, RateRepository rateRepository, RestHelper restHelper) {
		RateEntity metalRuEntity = rateRepository.getMetalRu();
		if (parse(restHelper.getRestResponse(url).answer())) {
			commit(metalRuEntity, rateRepository);
		} else {
			MetalRuMirrorParser metalRuMirrorParser = new MetalRuMirrorParser();
			if (metalRuMirrorParser.parse(restHelper.getRestResponse(mirror).answer())) {
				metalRuMirrorParser.commit(metalRuEntity, rateRepository);
			}
		}
	}

	@Override
	protected int entityId() {
		return RateEntity.METAL_RU_ROW;
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
