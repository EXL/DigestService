package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class BankUaManager extends RateManager {
	private final RateRepository rateRepository;
	private final RestHelper restHelper;

	public BankUaManager(RateRepository rateRepository, RestHelper restHelper) {
		this.rateRepository = rateRepository;
		this.restHelper = restHelper;
	}

	@Override
	public void commitRates(String url, String mirror) {
		BankUaParser bankUaParser = new BankUaParser();
		if (bankUaParser.parse(restHelper.getRestResponse(url).answer())) {
			commitAux(bankUaParser);
		} else {
			BankUaMirrorParser bankUaMirrorParser = new BankUaMirrorParser();
			if (bankUaMirrorParser.parse(restHelper.getRestResponse(mirror).answer())) {
				commitAux(bankUaMirrorParser);
			}
		}
	}

	private void commitAux(BankUaParser parser) {
		logRates(parser);
		BigDecimal prevUsd = null;
		RateEntity bankUaEntityFromDb = rateRepository.getBankUa();
		if (bankUaEntityFromDb != null) {
			prevUsd = bankUaEntityFromDb.getUsd();
		} else {
			bankUaEntityFromDb = new RateEntity();
		}

		bankUaEntityFromDb.setDate(parser.getDate());
		bankUaEntityFromDb.setUsd(parser.getUsd());
		bankUaEntityFromDb.setEur(parser.getEur());
		bankUaEntityFromDb.setKzt(parser.getKzt());
		bankUaEntityFromDb.setByn(parser.getByn());
		bankUaEntityFromDb.setRub(parser.getRub());
		bankUaEntityFromDb.setGbp(parser.getGbp());
		bankUaEntityFromDb.setPrev((prevUsd == null) ? parser.getUsd() : prevUsd);

		rateRepository.save(bankUaEntityFromDb);
	}
}
