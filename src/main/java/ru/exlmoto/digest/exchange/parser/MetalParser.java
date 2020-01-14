package ru.exlmoto.digest.exchange.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Getter
public abstract class MetalParser extends RateParser {
	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;

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
