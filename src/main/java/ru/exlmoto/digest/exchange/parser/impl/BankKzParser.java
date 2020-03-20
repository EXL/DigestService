package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.service.DatabaseService;

import java.math.BigDecimal;

public class BankKzParser extends BankParser {
	@Override
	protected ExchangeRateEntity getEntity(DatabaseService service) {
		return service.getBankKz().orElse(null);
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_KZ_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
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
		cny = parseValue(document, "CNY");
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
