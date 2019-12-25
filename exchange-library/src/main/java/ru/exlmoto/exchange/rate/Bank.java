package ru.exlmoto.exchange.rate;

import java.math.BigDecimal;

public abstract class Bank extends Rate {
	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal gbp = null;
	protected BigDecimal rub = null;
}
