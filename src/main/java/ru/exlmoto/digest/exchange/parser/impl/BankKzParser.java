package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class BankKzParser extends BankParser {
	@Override
	public void commitRates(String url, String mirror, RateRepository rateRepository, RestHelper restHelper) {
		if (parse(restHelper.getRestResponse(url).answer())) {
			commit(rateRepository.getBankKz(), rateRepository);
		}
	}

	@Override
	protected void commitAux(RateEntity entity) {
		entity.setRub(rub);
		entity.setUah(uah);
		entity.setByn(byn);
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "USD");
		eur = parseValue(document, "EUR");
		rub = parseValue(document, "RUB");
		byn = parseValue(document, "BYN");
		uah = parseValue(document, "UAH");
		gbp = parseValue(document, "GBP");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("title:contains(" + valueId + ")").parent();
		BigDecimal quant = new BigDecimal(filterCommas(element.selectFirst("quant").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("description").text()));
		return value.divide(quant, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("title:contains(USD)").parent();
		return element.selectFirst("pubDate").text();
	}
}
