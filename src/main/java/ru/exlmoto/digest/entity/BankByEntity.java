package ru.exlmoto.digest.entity;

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
@Table(name = "exchange_bank_by")
public class BankByEntity {
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
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

	public boolean checkAllValues() {
		return usd != null || eur != null || kzt != null || uah != null || rub != null || gbp != null;
	}
}
