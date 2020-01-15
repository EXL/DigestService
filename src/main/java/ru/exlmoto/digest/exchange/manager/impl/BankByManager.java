package ru.exlmoto.digest.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.entity.BankByEntity;
import ru.exlmoto.digest.repository.BankByRepository;
import ru.exlmoto.digest.rest.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankByManager extends RateManager {
	private final BankByRepository bankByRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		BankByParser bankByParser = new BankByParser();
		if (bankByParser.parse(restService.getRestResponse(url).answer())) {
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