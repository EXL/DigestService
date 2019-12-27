package ru.exlmoto.exchange.domain;

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
@Table(name = "exchange_metal_ru")
public class MetalRuEntity {
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Id
	private final int id = 1;
	private String date;
	private BigDecimal gold;
	private BigDecimal silver;
	private BigDecimal platinum;
	private BigDecimal palladium;
	private BigDecimal prev;

	public boolean checkAllValues() {
		return gold != null || silver != null || platinum != null || palladium != null;
	}
}
