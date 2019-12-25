package ru.exlmoto.exchange.rate;

import java.math.BigDecimal;

public abstract class Metal extends Rate {
	protected BigDecimal gold = null;
	protected BigDecimal silver = null;
	protected BigDecimal platinum = null;
	protected BigDecimal palladium = null;

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
