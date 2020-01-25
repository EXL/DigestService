package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.entity.BankByEntity;
import ru.exlmoto.digest.repository.BankByRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankByManager extends RateManager {
	private final BankByRepository bankByRepository;
	private final RestHelper restHelper;

	public BankByManager(BankByRepository bankByRepository, RestHelper restHelper) {
		this.bankByRepository = bankByRepository;
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
		BankByEntity bankByEntityFromDb = bankByRepository.getBankBy();
		if (bankByEntityFromDb != null) {
			prevUsd = bankByEntityFromDb.getUsd();
		}
		bankByRepository.save(
			new BankByEntity(
				parser.getDate(),
				parser.getUsd(),
				parser.getEur(),
				parser.getKzt(),
				parser.getRub(),
				parser.getUah(),
				parser.getGbp(),
				(prevUsd == null) ? parser.getUsd() : prevUsd
			)
		);
	}
}
