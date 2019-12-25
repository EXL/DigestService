package ru.exlmoto.exchange.rate;

import java.math.BigDecimal;

public abstract class BankEntity extends RateEntity {
	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal gbp = null;
	protected BigDecimal rub = null;

	public BigDecimal getUsd() {
		return usd;
	}

	public BigDecimal getEur() {
		return eur;
	}

	public BigDecimal getKzt() {
		return kzt;
	}

	public BigDecimal getByn() {
		return byn;
	}

	public BigDecimal getUah() {
		return uah;
	}

	public BigDecimal getGbp() {
		return gbp;
	}

	public BigDecimal getRub() {
		return rub;
	}
}
