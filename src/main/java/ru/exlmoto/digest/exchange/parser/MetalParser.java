package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public abstract class MetalParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(MetalParser.class);

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

	public BigDecimal getGold() {
		return gold;
	}

	public BigDecimal getSilver() {
		return silver;
	}

	public BigDecimal getPlatinum() {
		return platinum;
	}

	public BigDecimal getPalladium() {
		return palladium;
	}
}
