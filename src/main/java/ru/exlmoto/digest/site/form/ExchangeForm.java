package ru.exlmoto.digest.site.form;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

public class ExchangeForm {
	private ExchangeRateEntity rub;
	private ExchangeRateEntity uah;
	private ExchangeRateEntity byn;
	private ExchangeRateEntity kzt;
	private ExchangeRateEntity metal;

	public ExchangeRateEntity getRub() {
		return rub;
	}

	public void setRub(ExchangeRateEntity rub) {
		this.rub = rub;
	}

	public ExchangeRateEntity getUah() {
		return uah;
	}

	public void setUah(ExchangeRateEntity uah) {
		this.uah = uah;
	}

	public ExchangeRateEntity getByn() {
		return byn;
	}

	public void setByn(ExchangeRateEntity byn) {
		this.byn = byn;
	}

	public ExchangeRateEntity getKzt() {
		return kzt;
	}

	public void setKzt(ExchangeRateEntity kzt) {
		this.kzt = kzt;
	}

	public ExchangeRateEntity getMetal() {
		return metal;
	}

	public void setMetal(ExchangeRateEntity metal) {
		this.metal = metal;
	}
}
