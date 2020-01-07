package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.manager.RestManager;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankRuManager {
	private final BankRuRepository bankRuRepository;

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(new RestManager().getRawContent(url))) {
			log.info("==> Using BankRuParser.");
			commitAux(bankRuParser);
		} else {
			BankRuParser bankRuMirrorParser = new BankRuParser();
			if (bankRuMirrorParser.parse(new RestManager().getRawContent(mirror))) {
				log.info("==> Using mirror BankRuParser.");
				commitAux(bankRuMirrorParser);
			}
		}
	}

	private void commitAux(BankRuParser parser) {
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
