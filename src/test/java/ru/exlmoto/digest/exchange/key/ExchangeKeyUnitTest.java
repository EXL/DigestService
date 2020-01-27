package ru.exlmoto.digest.exchange.key;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExchangeKeyUnitTest {
	@Test
	public void testCheckExchangeKey() {
		assertNotNull(ExchangeKey.checkExchangeKey("bank_"));
		assertNotNull(ExchangeKey.checkExchangeKey("bank_ru"));
	}
}
