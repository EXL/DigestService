package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.digest.entity.RateEntity;

import java.math.BigDecimal;

public abstract class BankParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(BankParser.class);

	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal gbp = null;
	protected BigDecimal rub = null;

	@Override
	protected BigDecimal parsedPrevValue() {
		return usd;
	}

	@Override
	protected void commitGeneralValues(RateEntity entity) {
		entity.setUsd(usd);
		entity.setEur(eur);
		entity.setGbp(gbp);

		commitAux(entity);
	}

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

	protected abstract void commitAux(RateEntity entity);
}
