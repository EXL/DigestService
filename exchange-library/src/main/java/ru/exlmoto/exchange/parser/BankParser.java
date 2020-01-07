package ru.exlmoto.exchange.parser;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public abstract class BankParser extends RateParser {
	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal gbp = null;
	protected BigDecimal rub = null;
}
