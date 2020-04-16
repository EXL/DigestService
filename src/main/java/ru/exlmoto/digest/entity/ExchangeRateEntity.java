package ru.exlmoto.digest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "exchange_rate")
public class ExchangeRateEntity {
	public static final int BANK_RU_ROW = 1;
	public static final int BANK_UA_ROW = 2;
	public static final int BANK_BY_ROW = 3;
	public static final int BANK_KZ_ROW = 4;
	public static final int METAL_RU_ROW = 5;

	@Id
	private int id;

	@Column(nullable = false)
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
	private BigDecimal cny;

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
	private BigDecimal prevUsd;

	@Column
	private BigDecimal prevEur;

	@Column
	private BigDecimal prevByn;

	@Column
	private BigDecimal prevKzt;

	@Column
	private BigDecimal prevUah;

	@Column
	private BigDecimal prevRub;

	@Column
	private BigDecimal prevCny;

	@Column
	private BigDecimal prevGbp;

	@Column
	private BigDecimal prevGold;

	@Column
	private BigDecimal prevSilver;

	@Column
	private BigDecimal prevPlatinum;

	@Column
	private BigDecimal prevPalladium;

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

	public BigDecimal getCny() {
		return cny;
	}

	public void setCny(BigDecimal cny) {
		this.cny = cny;
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

	public BigDecimal getPrevUsd() {
		return prevUsd;
	}

	public void setPrevUsd(BigDecimal prevUsd) {
		this.prevUsd = prevUsd;
	}

	public BigDecimal getPrevEur() {
		return prevEur;
	}

	public void setPrevEur(BigDecimal prevEur) {
		this.prevEur = prevEur;
	}

	public BigDecimal getPrevByn() {
		return prevByn;
	}

	public void setPrevByn(BigDecimal prevByn) {
		this.prevByn = prevByn;
	}

	public BigDecimal getPrevKzt() {
		return prevKzt;
	}

	public void setPrevKzt(BigDecimal prevKzt) {
		this.prevKzt = prevKzt;
	}

	public BigDecimal getPrevUah() {
		return prevUah;
	}

	public void setPrevUah(BigDecimal prevUah) {
		this.prevUah = prevUah;
	}

	public BigDecimal getPrevRub() {
		return prevRub;
	}

	public void setPrevRub(BigDecimal prevRub) {
		this.prevRub = prevRub;
	}

	public BigDecimal getPrevCny() {
		return prevCny;
	}

	public void setPrevCny(BigDecimal prevCny) {
		this.prevCny = prevCny;
	}

	public BigDecimal getPrevGbp() {
		return prevGbp;
	}

	public void setPrevGbp(BigDecimal prevGbp) {
		this.prevGbp = prevGbp;
	}

	public BigDecimal getPrevGold() {
		return prevGold;
	}

	public void setPrevGold(BigDecimal prevGold) {
		this.prevGold = prevGold;
	}

	public BigDecimal getPrevSilver() {
		return prevSilver;
	}

	public void setPrevSilver(BigDecimal prevSilver) {
		this.prevSilver = prevSilver;
	}

	public BigDecimal getPrevPlatinum() {
		return prevPlatinum;
	}

	public void setPrevPlatinum(BigDecimal prevPlatinum) {
		this.prevPlatinum = prevPlatinum;
	}

	public BigDecimal getPrevPalladium() {
		return prevPalladium;
	}

	public void setPrevPalladium(BigDecimal prevPalladium) {
		this.prevPalladium = prevPalladium;
	}

	@Override
	public String toString() {
		return
			"ExchangeRateEntity{id=" + id +
			", date=" + date +
			", usd=" + usd +
			", eur=" + eur +
			", byn=" + byn +
			", kzt=" + kzt +
			", uah=" + uah +
			", rub=" + rub +
			", cny=" + cny +
			", gbp=" + gbp +
			", gold=" + gold +
			", silver=" + silver +
			", platinum=" + platinum +
			", palladium=" + palladium +
			", prevUsd=" + prevUsd +
			", prevEur=" + prevEur +
			", prevByn=" + prevByn +
			", prevKzt=" + prevKzt +
			", prevUah=" + prevUah +
			", prevRub=" + prevRub +
			", prevCny=" + prevCny +
			", prevGbp=" + prevGbp +
			", prevGold=" + prevGold +
			", prevSilver=" + prevSilver +
			", prevPlatinum=" + prevPlatinum +
			", prevPalladium=" + prevPalladium +
			"}";
	}
}
