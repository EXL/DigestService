package ru.exlmoto.digest.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.manager.impl.BankRuManager;
import ru.exlmoto.digest.exchange.manager.impl.BankUaManager;
import ru.exlmoto.digest.exchange.manager.impl.MetalRuManager;

@SpringBootTest
class RateGeneralManagerTest {
	@Value("${exchange.bank-ru-mirror}")
	private String bankRuMirror;

	@Value("${exchange.bank-ua-mirror}")
	private String bankUaMirror;

	@Value("${exchange.metal-ru-mirror}")
	private String metalRuMirror;

	@Autowired
	private BankRuManager bankRuManager;

	@Autowired
	private BankUaManager bankUaManager;

	@Autowired
	private MetalRuManager metalRuManager;

	@Test
	public void testRateMirrors() {
		bankRuManager.commitRates(null, bankRuMirror);
		bankUaManager.commitRates(null, bankUaMirror);
		metalRuManager.commitRates(null, metalRuMirror);
	}
}
