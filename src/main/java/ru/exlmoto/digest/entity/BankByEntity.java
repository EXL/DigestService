package ru.exlmoto.digest.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "exchange_bank_by")
public class BankByEntity {
	@Id
	private final int id = 1;

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

	public BankByEntity(String date,
	                    BigDecimal usd,
	                    BigDecimal eur,
	                    BigDecimal kzt,
	                    BigDecimal uah,
	                    BigDecimal rub,
	                    BigDecimal gbp,
	                    BigDecimal prev) {
		this.date = date;
		this.usd = usd;
		this.eur = eur;
		this.kzt = kzt;
		this.uah = uah;
		this.rub = rub;
		this.gbp = gbp;
		this.prev = prev;
	}

	public boolean checkAllValues() {
		return usd != null || eur != null || kzt != null || uah != null || rub != null || gbp != null;
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

	@Override
	public String toString() {
		return
			"BankByEntity{id=" + id +
			", date=" + date +
			", usd=" + usd +
			", eur=" + eur +
			", kzt=" + kzt +
			", uah=" + uah +
			", rub=" + rub +
			", gbp=" + gbp +
			", prev=" + prev +
			"}";
	}
}
