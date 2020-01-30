package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

import java.math.BigDecimal;

public abstract class MetalParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(MetalParser.class);

	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;

	@Override
	protected BigDecimal parsedPrevValue() {
		return gold;
	}

	@Override
	protected void commitGeneralValues(ExchangeRateEntity entity) {
		entity.setGold(gold);
		entity.setSilver(silver);
		entity.setPlatinum(platinum);
		entity.setPalladium(palladium);
	}

	@Override
	protected boolean checkParsedValues() {
		return gold != null || silver != null || platinum != null || palladium != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
			"===> Date: %s, Gold: %s, Silver: %s, Platinum: %s, Palladium: %s",
			date, gold, silver, platinum, palladium
			)
		);
	}
}
