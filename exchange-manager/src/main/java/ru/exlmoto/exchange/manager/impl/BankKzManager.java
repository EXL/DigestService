package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankKzEntity;
import ru.exlmoto.exchange.parser.impl.BankKzParser;
import ru.exlmoto.exchange.repository.BankKzRepository;
import ru.exlmoto.exchange.manager.RateManager;
import ru.exlmoto.rest.service.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankKzManager extends RateManager {
	private final BankKzRepository bankKzRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		BankKzParser bankKzParser = new BankKzParser();
		if (bankKzParser.parse(restService.getRawContent(url))) {
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
