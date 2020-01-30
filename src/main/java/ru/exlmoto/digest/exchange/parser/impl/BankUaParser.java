package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.repository.ExchangeRateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class BankUaParser extends BankParser {
	@Override
	public void commitRates(String url, String mirror, ExchangeRateRepository repository, RestHelper rest) {
		ExchangeRateEntity bankUaEntity = repository.getBankUa();
		if (parse(rest.getRestResponse(url).answer())) {
			commit(bankUaEntity, repository);
		} else {
			BankUaMirrorParser bankUaMirrorParser = new BankUaMirrorParser();
			if (bankUaMirrorParser.parse(rest.getRestResponse(mirror).answer())) {
				bankUaMirrorParser.commit(bankUaEntity, repository);
			}
		}
	}

	@Override
	protected int entityId() {
		return ExchangeRateEntity.BANK_UA_ROW;
	}

	@Override
	protected void commitAux(ExchangeRateEntity entity) {
		entity.setRub(rub);
		entity.setByn(byn);
		entity.setKzt(kzt);
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "840");
		eur = parseValue(document, "978");
		kzt = parseValue(document, "398");
		byn = parseValue(document, "933");
		rub = parseValue(document, "643");
		gbp = parseValue(document, "826");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("r030:contains(" + valueId + ")").parent();
		return new BigDecimal(filterCommas(element.selectFirst("rate").text()));
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("r030:contains(840)").parent();
		return element.selectFirst("exchangedate").text();
	}
}
