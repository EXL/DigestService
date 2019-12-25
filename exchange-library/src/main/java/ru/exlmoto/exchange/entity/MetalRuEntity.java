package ru.exlmoto.exchange.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "exchange_metal_ru")
public class MetalRuEntity {
	@Id
	private final Long id = 1L;

	private String date;
	private BigDecimal gold;
	private BigDecimal silver;
	private BigDecimal platinum;
	private BigDecimal palladium;
	private BigDecimal prev;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public void determineAll(String date, BigDecimal gold, BigDecimal silver,
	                         BigDecimal platinum, BigDecimal palladium, BigDecimal prev) {
		this.date = date;
		this.gold = gold;
		this.silver = silver;
		this.platinum = platinum;
		this.palladium = palladium;
		this.prev = prev;
	}
}
