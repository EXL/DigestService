package ru.exlmoto.exchange.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exchange_bank_ua")
public class BankUaEntity {
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Id
	private final int id = 1;
	private String date;
	private BigDecimal usd;
	private BigDecimal eur;
	private BigDecimal kzt;
	private BigDecimal byn;
	private BigDecimal rub;
	private BigDecimal gbp;
	private BigDecimal prev;
}
