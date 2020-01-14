package ru.exlmoto.digest.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.entity.MetalRuEntity;
import ru.exlmoto.digest.repository.MetalRuRepository;
import ru.exlmoto.digest.rest.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class MetalRuManager extends RateManager {
	private final MetalRuRepository metalRuRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		MetalRuParser metalRuParser = new MetalRuParser();
		if (metalRuParser.parse(restService.getRestResponse(url).answer())) {
			commitAux(metalRuParser);
		} else {
			MetalRuMirrorParser metalRuMirrorParser = new MetalRuMirrorParser();
			if (metalRuMirrorParser.parse(restService.getRestResponse(mirror).answer())) {
				commitAux(metalRuMirrorParser);
			}
		}
	}

	private void commitAux(MetalRuParser parser) {
		logRates(parser);
		BigDecimal prevGold = null;
		MetalRuEntity metalRuEntityFromDb = metalRuRepository.getMetalRu();
		if (metalRuEntityFromDb != null) {
			prevGold = metalRuEntityFromDb.getGold();
		}
		metalRuRepository.save(
			new MetalRuEntity(
				parser.getDate(),
				parser.getGold(),
				parser.getSilver(),
				parser.getPlatinum(),
				parser.getPalladium(),
				(prevGold == null) ? parser.getGold() : prevGold
			)
		);
	}
}
