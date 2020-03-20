package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.RateService;

import java.math.BigDecimal;

public class BankByParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(RateService service) {
		return service.getBankBy().orElse(null);
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "145");
		eur = parseValue(document, "292");
		kzt = parseValue(document, "301");
		rub = parseValue(document, "298");
		uah = parseValue(document, "290");
		cny = parseValue(document, "304");
		gbp = parseValue(document, "143");
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_BY_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
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
