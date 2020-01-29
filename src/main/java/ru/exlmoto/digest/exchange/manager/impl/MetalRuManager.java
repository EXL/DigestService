package ru.exlmoto.digest.exchange.manager.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.RateEntity;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

import java.math.BigDecimal;

@Component
public class MetalRuManager extends RateManager {
	private final RateRepository rateRepository;
	private final RestHelper restHelper;

	public MetalRuManager(RateRepository rateRepository, RestHelper restHelper) {
		this.rateRepository = rateRepository;
		this.restHelper = restHelper;
	}

	@Override
	public void commitRates(String url, String mirror) {
		MetalRuParser metalRuParser = new MetalRuParser();
		if (metalRuParser.parse(restHelper.getRestResponse(url).answer())) {
			commitAux(metalRuParser);
		} else {
			MetalRuMirrorParser metalRuMirrorParser = new MetalRuMirrorParser();
			if (metalRuMirrorParser.parse(restHelper.getRestResponse(mirror).answer())) {
				commitAux(metalRuMirrorParser);
			}
		}
	}

	private void commitAux(MetalRuParser parser) {
		logRates(parser);
		BigDecimal prevGold = null;
		RateEntity metalRuEntityFromDb = rateRepository.getMetalRu();
		if (metalRuEntityFromDb != null) {
			prevGold = metalRuEntityFromDb.getGold();
		} else {
			metalRuEntityFromDb = new RateEntity();
		}

		metalRuEntityFromDb.setDate(parser.getDate());
		metalRuEntityFromDb.setGold(parser.getGold());
		metalRuEntityFromDb.setSilver(parser.getSilver());
		metalRuEntityFromDb.setPlatinum(parser.getPlatinum());
		metalRuEntityFromDb.setPalladium(parser.getPalladium());
		metalRuEntityFromDb.setPrev((prevGold == null) ? parser.getGold() : prevGold);

		rateRepository.save(metalRuEntityFromDb);
	}
}
