package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankKzManager extends RateManager {
	private final RateRepository rateRepository;
	private final RestHelper restHelper;

	public BankKzManager(RateRepository rateRepository, RestHelper restHelper) {
		this.rateRepository = rateRepository;
		this.restHelper = restHelper;
	}

	@Override
	public void commitRates(String url, String mirror) {
		BankKzParser bankKzParser = new BankKzParser();
		if (bankKzParser.parse(restHelper.getRestResponse(url).answer())) {
			commitAux(bankKzParser);
		}
	}

	private void commitAux(BankKzParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		RateEntity bankKzEntityFromDb = rateRepository.getBankKz();
		if (bankKzEntityFromDb != null) {
			prevUsd = bankKzEntityFromDb.getUsd();
		} else {
			bankKzEntityFromDb = new RateEntity();
		}

		bankKzEntityFromDb.setDate(parser.getDate());
		bankKzEntityFromDb.setUsd(parser.getUsd());
		bankKzEntityFromDb.setEur(parser.getEur());
		bankKzEntityFromDb.setRub(parser.getRub());
		bankKzEntityFromDb.setByn(parser.getByn());
		bankKzEntityFromDb.setUah(parser.getUah());
		bankKzEntityFromDb.setGbp(parser.getGbp());
		bankKzEntityFromDb.setPrev((prevUsd == null) ? parser.getUsd() : prevUsd);

		rateRepository.save(bankKzEntityFromDb);
	}
}
