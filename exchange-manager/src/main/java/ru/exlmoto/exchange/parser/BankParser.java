package ru.exlmoto.exchange.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Getter
public abstract class BankParser extends RateParser {
	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal gbp = null;
	protected BigDecimal rub = null;

	@Override
	protected boolean checkParsedValues() {
		return usd != null || eur != null || kzt != null || byn != null || rub != null || uah != null || gbp != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
			"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, UAH: %s, GBP: %s",
			date, usd, eur, kzt, byn, rub, uah, gbp
			)
		);
	}
}
