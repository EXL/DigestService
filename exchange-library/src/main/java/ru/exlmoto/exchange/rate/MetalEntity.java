package ru.exlmoto.exchange.rate;

import java.math.BigDecimal;

public abstract class MetalEntity extends RateEntity {
	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;
}
