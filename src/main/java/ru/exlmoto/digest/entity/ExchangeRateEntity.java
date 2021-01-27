/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

	@Column(precision = 20, scale = 8)
	private BigDecimal usd;

	@Column(precision = 20, scale = 8)
	private BigDecimal eur;

	@Column(precision = 20, scale = 8)
	private BigDecimal byn;

	@Column(precision = 20, scale = 8)
	private BigDecimal kzt;

	@Column(precision = 20, scale = 8)
	private BigDecimal uah;

	@Column(precision = 20, scale = 8)
	private BigDecimal rub;

	@Column(precision = 20, scale = 8)
	private BigDecimal cny;

	@Column(precision = 20, scale = 8)
	private BigDecimal gbp;

	@Column(precision = 20, scale = 8)
	private BigDecimal gold;

	@Column(precision = 20, scale = 8)
	private BigDecimal silver;

	@Column(precision = 20, scale = 8)
	private BigDecimal platinum;

	@Column(precision = 20, scale = 8)
	private BigDecimal palladium;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevUsd;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevEur;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevByn;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevKzt;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevUah;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevRub;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevCny;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevGbp;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevGold;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevSilver;

	@Column(precision = 20, scale = 8)
	private BigDecimal prevPlatinum;

	@Column(precision = 20, scale = 8)
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
