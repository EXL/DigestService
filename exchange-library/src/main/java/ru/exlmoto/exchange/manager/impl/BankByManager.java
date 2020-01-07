package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.parser.impl.BankByParser;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.manager.RestManager;
import ru.exlmoto.exchange.manager.RateManager;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankByManager extends RateManager {
	private final BankByRepository bankByRepository;

	@Override
	public void commitRates(String url, String mirror) {
		BankByParser bankByParser = new BankByParser();
		if (bankByParser.parse(new RestManager().getRawContent(url))) {
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
