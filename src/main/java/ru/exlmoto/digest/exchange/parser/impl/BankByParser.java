package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

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
	public void commitRates(String url, String mirror, RateRepository rateRepository, RestHelper restHelper) {
		if (parse(restHelper.getRestResponse(url).answer())) {
			commit(rateRepository.getBankBy(), rateRepository);
		}
	}

	@Override
	protected void commitAux(RateEntity entity) {
		entity.setRub(rub);
		entity.setUah(uah);
		entity.setKzt(kzt);
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
}
