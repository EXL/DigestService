package ru.exlmoto.digest.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.entity.BankUaEntity;
import ru.exlmoto.digest.repository.BankUaRepository;
import ru.exlmoto.digest.rest.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class BankUaManager extends RateManager {
	private final BankUaRepository bankUaRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		BankUaParser bankUaParser = new BankUaParser();
		if (bankUaParser.parse(restService.getRestResponse(url).answer())) {
			commitAux(bankUaParser);
		} else {
			BankUaMirrorParser bankUaMirrorParser = new BankUaMirrorParser();
			if (bankUaMirrorParser.parse(restService.getRestResponse(url).answer())) {
				commitAux(bankUaMirrorParser);
			}
		}
	}

	private void commitAux(BankUaParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		BankUaEntity bankUaEntityFromDb = bankUaRepository.getBankUa();
		if (bankUaEntityFromDb != null) {
			prevUsd = bankUaEntityFromDb.getUsd();
		}
		bankUaRepository.save(
			new BankUaEntity(
				parser.getDate(),
				parser.getUsd(),
				parser.getEur(),
				parser.getKzt(),
				parser.getByn(),
				parser.getRub(),
				parser.getGbp(),
				(prevUsd == null) ? parser.getUsd() : prevUsd
			)
		);
	}
}
