package ru.exlmoto.digest.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.entity.BankRuEntity;
import ru.exlmoto.digest.repository.BankRuRepository;
import ru.exlmoto.digest.rest.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankRuManager extends RateManager {
	private final BankRuRepository bankRuRepository;
	private final RestService restService;

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(restService.getRestResponse(url).answer())) {
			commitAux(bankRuParser);
		} else {
			BankRuParser bankRuMirrorParser = new BankRuParser();
			bankRuMirrorParser.setMirror(true);
			if (bankRuMirrorParser.parse(restService.getRestResponse(mirror).answer())) {
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
