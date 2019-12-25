package ru.exlmoto.exchange.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "exchange_bank_by")
public class BankByEntity {
	@Id
	private final Long id = 1L;

	private String date;
	private BigDecimal usd;
	private BigDecimal eur;
	private BigDecimal kzt;
	private BigDecimal uah;
	private BigDecimal rub;
	private BigDecimal gbp;
	private BigDecimal prev;

	public BankByEntity() {

	}

	public BankByEntity(String date, BigDecimal usd, BigDecimal eur, BigDecimal kzt,
	                    BigDecimal rub, BigDecimal uah, BigDecimal gbp, BigDecimal prev) {
		this.date = date;
		this.usd = usd;
		this.eur = eur;
		this.kzt = kzt;
		this.rub = rub;
		this.uah = uah;
		this.gbp = gbp;
		this.prev = prev;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getUsd() {
		return usd;
	}

	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}

	public BigDecimal getEur() {
		return eur;
	}

	public void setEur(BigDecimal eur) {
		this.eur = eur;
	}

	public BigDecimal getKzt() {
		return kzt;
	}

	public void setKzt(BigDecimal kzt) {
		this.kzt = kzt;
	}

	public BigDecimal getUah() {
		return uah;
	}

	public void setUah(BigDecimal uah) {
		this.uah = uah;
	}

	public BigDecimal getRub() {
		return rub;
	}

	public void setRub(BigDecimal rub) {
		this.rub = rub;
	}

	public BigDecimal getGbp() {
		return gbp;
	}

	public void setGbp(BigDecimal gbp) {
		this.gbp = gbp;
	}

	public BigDecimal getPrev() {
		return prev;
	}

	public void setPrev(BigDecimal prev) {
		this.prev = prev;
	}
}
