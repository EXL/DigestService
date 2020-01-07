package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankRuEntity;
import ru.exlmoto.exchange.parser.impl.BankRuParser;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.manager.RestManager;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BankRuManager {
	private final BankRuRepository bankRuRepository;

	public void commitRates(String url, String mirror) {
		BankRuParser bankRuParser = new BankRuParser();
		if (bankRuParser.parse(new RestManager().getRawContent(url)) ||
			bankRuParser.parse(new RestManager().getRawContent(mirror))) {
			BigDecimal prevUsd = null;
			BankRuEntity bankRuEntityFromDb = bankRuRepository.getBankRu();
			if (bankRuEntityFromDb != null) {
				prevUsd = bankRuEntityFromDb.getUsd();
			}
			bankRuRepository.save(
				new BankRuEntity(
					bankRuParser.getDate(),
					bankRuParser.getUsd(),
					bankRuParser.getEur(),
					bankRuParser.getKzt(),
					bankRuParser.getKzt(),
					bankRuParser.getUah(),
					bankRuParser.getGbp(),
					(prevUsd == null) ? bankRuParser.getUsd() : prevUsd
				)
			);
		}
	}
}
