package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankKzEntity;
import ru.exlmoto.exchange.parser.impl.BankKzParser;
import ru.exlmoto.exchange.repository.BankKzRepository;
import ru.exlmoto.exchange.manager.RestManager;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankKzManager {
	private final BankKzRepository bankKzRepository;

	public void commitRates(String url) {
		BankKzParser bankKzParser = new BankKzParser();
		if (bankKzParser.parse(new RestManager().getRawContent(url))) {
			log.info("==> Using BankKzParser.");
			BigDecimal prevUsd = null;
			BankKzEntity bankKzEntityFromDb = bankKzRepository.getBankKz();
			if (bankKzEntityFromDb != null) {
				prevUsd = bankKzEntityFromDb.getUsd();
			}
			bankKzRepository.save(
				new BankKzEntity(
					bankKzParser.getDate(),
					bankKzParser.getUsd(),
					bankKzParser.getEur(),
					bankKzParser.getRub(),
					bankKzParser.getByn(),
					bankKzParser.getUah(),
					bankKzParser.getGbp(),
					(prevUsd == null) ? bankKzParser.getUsd() : prevUsd
				)
			);
		}
	}
}
