package ru.exlmoto.exchange.parser;

import java.math.BigDecimal;

public abstract class Metal extends Rate {
	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;
}