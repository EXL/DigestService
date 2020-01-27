package ru.exlmoto.digest.exchange.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ExchangeKey {
	bank_ru,
	bank_ua,
	bank_by,
	bank_kz,
	metal_ru;

	private static final Logger log = LoggerFactory.getLogger(ExchangeKey.class);

	public static ExchangeKey checkExchangeKey(String key) {
		try {
			return ExchangeKey.valueOf(key);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong exchange key: '%s', return first default '%s'.", key, bank_ru), iae);
			return bank_ru;
		}
	}
}
