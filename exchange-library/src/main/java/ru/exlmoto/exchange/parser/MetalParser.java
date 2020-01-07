package ru.exlmoto.exchange.parser;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public abstract class MetalParser extends RateParser {
	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;
}
