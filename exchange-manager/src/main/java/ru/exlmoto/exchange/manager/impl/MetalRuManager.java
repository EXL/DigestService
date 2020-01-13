package ru.exlmoto.exchange.manager.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.MetalRuEntity;
import ru.exlmoto.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.exchange.repository.MetalRuRepository;
import ru.exlmoto.exchange.manager.RateManager;
import ru.exlmoto.rest.service.RestService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class MetalRuManager extends RateManager {
	private final MetalRuRepository metalRuRepository;
	private final RestService restService;

	@Override
	public void commitRates(String url, String mirror) {
		MetalRuParser metalRuParser = new MetalRuParser();
		if (metalRuParser.parse(restService.getRawContent(url))) {
			commitAux(metalRuParser);
		} else {
			MetalRuMirrorParser metalRuMirrorParser = new MetalRuMirrorParser();
			if (metalRuMirrorParser.parse(restService.getRawContent(mirror))) {
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
