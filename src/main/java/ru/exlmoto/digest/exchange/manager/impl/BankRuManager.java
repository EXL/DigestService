package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankRuManager extends RateManager {
	private final RateRepository rateRepository;
	private final RestHelper restHelper;

	public BankRuManager(RateRepository rateRepository, RestHelper restHelper) {
		this.rateRepository = rateRepository;
		this.restHelper = restHelper;
	}

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(restHelper.getRestResponse(url).answer())) {
			commitAux(bankRuParser);
		} else {
			BankRuParser bankRuMirrorParser = new BankRuParser();
			bankRuMirrorParser.setMirror(true);
			if (bankRuMirrorParser.parse(restHelper.getRestResponse(mirror).answer())) {
				commitAux(bankRuMirrorParser);
			}
		}
	}

	private void commitAux(BankRuParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		RateEntity bankRuEntityFromDb = rateRepository.getBankRu();
		if (bankRuEntityFromDb != null) {
			prevUsd = bankRuEntityFromDb.getUsd();
		} else {
			bankRuEntityFromDb = new RateEntity();
		}

		bankRuEntityFromDb.setDate(parser.getDate());
		bankRuEntityFromDb.setUsd(parser.getUsd());
		bankRuEntityFromDb.setEur(parser.getEur());
		bankRuEntityFromDb.setKzt(parser.getKzt());
		bankRuEntityFromDb.setByn(parser.getByn());
		bankRuEntityFromDb.setUah(parser.getUah());
		bankRuEntityFromDb.setGbp(parser.getGbp());
		bankRuEntityFromDb.setPrev((prevUsd == null) ? parser.getUsd() : prevUsd);

		rateRepository.save(bankRuEntityFromDb);
	}
}
