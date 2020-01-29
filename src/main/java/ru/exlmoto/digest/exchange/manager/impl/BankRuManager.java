package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankRuManager extends RateManager {
	private final BankRuRepository bankRuRepository;
	private final RestHelper restHelper;

	public BankRuManager(BankRuRepository bankRuRepository, RestHelper restHelper) {
		this.bankRuRepository = bankRuRepository;
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
