package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.manager.RateManager;
import ru.exlmoto.rest.service.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankRuManager extends RateManager {
	private final BankRuRepository bankRuRepository;
	private final RestService restService;

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(restService.getRawContent(url))) {
			commitAux(bankRuParser);
		} else {
			BankRuParser bankRuMirrorParser = new BankRuParser();
			bankRuMirrorParser.setMirror(true);
			if (bankRuMirrorParser.parse(restService.getRawContent(mirror))) {
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
