package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.parser.impl.BankByParser;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.manager.RestManager;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankByManager {
	private final BankByRepository bankByRepository;

	public void commitRates(String url) {
		BankByParser bankByParser = new BankByParser();
		if (bankByParser.parse(new RestManager().getRawContent(url))) {
			log.info("==> Using BankByParser.");
			BigDecimal prevUsd = null;
			BankByEntity bankByEntityFromDb = bankByRepository.getBankBy();
			if (bankByEntityFromDb != null) {
				prevUsd = bankByEntityFromDb.getUsd();
			}
			bankByRepository.save(
				new BankByEntity(
					bankByParser.getDate(),
					bankByParser.getUsd(),
					bankByParser.getEur(),
					bankByParser.getKzt(),
					bankByParser.getRub(),
					bankByParser.getUah(),
					bankByParser.getGbp(),
					(prevUsd == null) ? bankByParser.getUsd() : prevUsd
				)
			);
		}
	}
}
