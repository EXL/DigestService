package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class BankUaParser extends BankParser {
	@Override
	public void commitRates(String url, String mirror, RateRepository rateRepository, RestHelper restHelper) {
		RateEntity bankUaEntity = rateRepository.getBankUa();
		if (parse(restHelper.getRestResponse(url).answer())) {
			commit(bankUaEntity, rateRepository);
		} else {
			BankUaMirrorParser bankUaMirrorParser = new BankUaMirrorParser();
			if (bankUaMirrorParser.parse(restHelper.getRestResponse(mirror).answer())) {
				bankUaMirrorParser.commit(bankUaEntity, rateRepository);
			}
		}
	}

	@Override
	protected int entityId() {
		return RateEntity.BANK_UA_ROW;
	}

	@Override
	protected void commitAux(RateEntity entity) {
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
