package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankUaEntity;
import ru.exlmoto.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.exchange.parser.impl.BankUaParser;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.manager.RestManager;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankUaManager {
	private final BankUaRepository bankUaRepository;

	public void commitRates(String url, String mirror) {
		BankUaParser bankUaParser = new BankUaParser();
		if (bankUaParser.parse(new RestManager().getRawContent(url))) {
			log.info("==> Using BankUaParser.");
			commitAux(bankUaParser);
		} else {
			BankUaMirrorParser bankUaMirrorParser = new BankUaMirrorParser();
			if (bankUaMirrorParser.parse(new RestManager().getRawContent(mirror))) {
				log.info("==> Using mirror BankUaMirrorParser.");
				commitAux(bankUaMirrorParser);
			}
		}
	}

	private void commitAux(BankUaParser parser) {
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
