package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankKzManager extends RateManager {
	private final BankKzRepository bankKzRepository;
	private final RestHelper restHelper;

	public BankKzManager(BankKzRepository bankKzRepository, RestHelper restHelper) {
		this.bankKzRepository = bankKzRepository;
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
		BankKzEntity bankKzEntityFromDb = bankKzRepository.getBankKz();
		if (bankKzEntityFromDb != null) {
			prevUsd = bankKzEntityFromDb.getUsd();
		}
		bankKzRepository.save(
			new BankKzEntity(
				parser.getDate(),
				parser.getUsd(),
				parser.getEur(),
				parser.getRub(),
				parser.getByn(),
				parser.getUah(),
				parser.getGbp(),
				(prevUsd == null) ? parser.getUsd() : prevUsd
			)
		);
	}
}
