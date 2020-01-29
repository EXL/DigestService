package ru.exlmoto.digest.exchange.parser.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.BankParser;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

public class BankRuParser extends BankParser {
	@Override
	public void commitRates(String url, String mirror, RateRepository rateRepository, RestHelper restHelper) {
		RateEntity bankRuEntity = rateRepository.getBankRu();
		if (parse(restHelper.getRestResponse(url).answer())) {
			commit(bankRuEntity, rateRepository);
		} else {
			this.mirror = true;
			if (parse(restHelper.getRestResponse(mirror).answer())) {
				commit(bankRuEntity, rateRepository);
			}
		}
	}

	@Override
	protected int entityId() {
		return RateEntity.BANK_RU_ROW;
	}

	@Override
	protected void commitAux(RateEntity entity) {
		entity.setUah(uah);
		entity.setByn(byn);
		entity.setKzt(kzt);
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "R01235");
		eur = parseValue(document, "R01239");
		kzt = parseValue(document, "R01335");
		byn = parseValue(document, "R01090B");
		uah = parseValue(document, "R01720");
		gbp = parseValue(document, "R01035");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Valute[ID=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Nominal").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Value").text()));
		return value.divide(nominal, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("ValCurs").attr("Date");
	}
}
