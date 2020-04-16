package ru.exlmoto.digest.exchange.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.exlmoto.digest.entity.ExchangeRateEntity;

import java.math.BigDecimal;

public abstract class BankParser extends RateParser {
	private final Logger log = LoggerFactory.getLogger(BankParser.class);

	protected BigDecimal usd = null;
	protected BigDecimal eur = null;
	protected BigDecimal kzt = null;
	protected BigDecimal byn = null;
	protected BigDecimal uah = null;
	protected BigDecimal rub = null;
	protected BigDecimal cny = null;
	protected BigDecimal gbp = null;

	@Override
	protected void setPrevGeneralValues(ExchangeRateEntity entity, BigDecimal prevValue) {
		if (prevValue != null) {
			entity.setPrevUsd(entity.getUsd());
			entity.setPrevEur(entity.getEur());
			entity.setPrevGbp(entity.getGbp());
			entity.setPrevCny(entity.getCny());
			setPrevValuesAux(entity);
		} else {
			entity.setPrevUsd(usd);
			entity.setPrevEur(eur);
			entity.setPrevGbp(gbp);
			entity.setPrevCny(cny);
			setPrevValuesFirstAux(entity);
		}
	}

	@Override
	protected void commitGeneralValues(ExchangeRateEntity entity) {
		entity.setUsd(usd);
		entity.setEur(eur);
		entity.setGbp(gbp);
		entity.setCny(cny);

		commitAux(entity);
	}

	@Override
	protected boolean checkParsedValues() {
		return usd != null && eur != null && cny != null && gbp != null;
	}

	@Override
	public void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, UAH: %s, CNY: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, uah, cny, gbp
			)
		);
	}

	protected abstract void commitAux(ExchangeRateEntity entity);

	protected abstract void setPrevValuesFirstAux(ExchangeRateEntity entity);

	protected abstract void setPrevValuesAux(ExchangeRateEntity entity);
}
