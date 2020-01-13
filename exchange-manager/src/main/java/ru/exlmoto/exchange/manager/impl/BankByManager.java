package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.parser.impl.BankByParser;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.manager.RateManager;
import ru.exlmoto.rest.service.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankByManager extends RateManager {
	private final BankByRepository bankByRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		BankByParser bankByParser = new BankByParser();
		if (bankByParser.parse(restService.getRawContent(url))) {
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
