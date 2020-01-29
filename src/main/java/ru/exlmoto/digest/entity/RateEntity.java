package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "exchange_rate")
public class RateEntity {
	public static final int BANK_RU_ROW = 1;
	public static final int BANK_UA_ROW = 2;
	public static final int BANK_BY_ROW = 3;
	public static final int BANK_KZ_ROW = 4;
	public static final int METAL_RU_ROW = 5;

	@Id
	private int id;

	@Column
	private String date;

	@Column
	private BigDecimal usd;

	@Column
	private BigDecimal eur;

	@Column
	private BigDecimal byn;

	@Column
	private BigDecimal kzt;

	@Column
	private BigDecimal uah;

	@Column
	private BigDecimal rub;

	@Column
	private BigDecimal gbp;

	@Column
	private BigDecimal gold;

	@Column
	private BigDecimal silver;

	@Column
	private BigDecimal platinum;

	@Column
	private BigDecimal palladium;

	@Column
	private BigDecimal prev;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public BigDecimal getByn() {
		return byn;
	}

	public void setByn(BigDecimal byn) {
		this.byn = byn;
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

	public BigDecimal getGold() {
		return gold;
	}

	public void setGold(BigDecimal gold) {
		this.gold = gold;
	}

	public BigDecimal getSilver() {
		return silver;
	}

	public void setSilver(BigDecimal silver) {
		this.silver = silver;
	}

	public BigDecimal getPlatinum() {
		return platinum;
	}

	public void setPlatinum(BigDecimal platinum) {
		this.platinum = platinum;
	}

	public BigDecimal getPalladium() {
		return palladium;
	}

	public void setPalladium(BigDecimal palladium) {
		this.palladium = palladium;
	}

	public BigDecimal getPrev() {
		return prev;
	}

	public void setPrev(BigDecimal prev) {
		this.prev = prev;
	}
}
