package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.manager.RestManager;
import ru.exlmoto.exchange.manager.RateManager;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankRuManager extends RateManager {
	private final BankRuRepository bankRuRepository;

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(new RestManager().getRawContent(url))) {
			commitAux(bankRuParser);
		} else {
			BankRuParser bankRuMirrorParser = new BankRuParser();
			bankRuMirrorParser.setMirror(true);
			if (bankRuMirrorParser.parse(new RestManager().getRawContent(mirror))) {
				commitAux(bankRuMirrorParser);
			}
		}
	}

	private void commitAux(BankRuParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		BankRuEntity bankRuEntityFromDb = bankRuRepository.getBankRu();
		if (bankRuEntityFromDb != null) {
			prevUsd = bankRuEntityFromDb.getUsd();
		}
		bankRuRepository.save(
			new BankRuEntity(
				parser.getDate(),
				parser.getUsd(),
				parser.getEur(),
				parser.getKzt(),
				parser.getKzt(),
				parser.getUah(),
				parser.getGbp(),
				(prevUsd == null) ? parser.getUsd() : prevUsd
			)
		);
	}
}
