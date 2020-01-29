package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankByManager extends RateManager {
	private final RateRepository rateRepository;
	private final RestHelper restHelper;

	public BankByManager(RateRepository rateRepository, RestHelper restHelper) {
		this.rateRepository = rateRepository;
		this.restHelper = restHelper;
	}

	@Override
	public void commitRates(String url, String mirror) {
		BankByParser bankByParser = new BankByParser();
		if (bankByParser.parse(restHelper.getRestResponse(url).answer())) {
			commitAux(bankByParser);
		}
	}

	private void commitAux(BankByParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		RateEntity bankByEntityFromDb = rateRepository.getBankBy();
		if (bankByEntityFromDb != null) {
			prevUsd = bankByEntityFromDb.getUsd();
		} else {
			bankByEntityFromDb = new RateEntity();
		}

		bankByEntityFromDb.setDate(parser.getDate());
		bankByEntityFromDb.setUsd(parser.getUsd());
		bankByEntityFromDb.setEur(parser.getEur());
		bankByEntityFromDb.setKzt(parser.getKzt());
		bankByEntityFromDb.setRub(parser.getRub());
		bankByEntityFromDb.setUah(parser.getUah());
		bankByEntityFromDb.setGbp(parser.getGbp());
		bankByEntityFromDb.setPrev((prevUsd == null) ? parser.getUsd() : prevUsd);

		rateRepository.save(bankByEntityFromDb);
	}
}
