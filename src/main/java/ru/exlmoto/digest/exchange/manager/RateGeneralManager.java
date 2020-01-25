package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.exchange.manager.impl.BankByManager;
import ru.exlmoto.digest.exchange.manager.impl.BankKzManager;
import ru.exlmoto.digest.exchange.manager.impl.BankRuManager;
import ru.exlmoto.digest.exchange.manager.impl.BankUaManager;
import ru.exlmoto.digest.exchange.manager.impl.MetalRuManager;

@Component
public class RateGeneralManager {
	private final Logger log = LoggerFactory.getLogger(RateGeneralManager.class);

	private final ExchangeConfiguration config;

	private final BankRuManager bankRuManager;
	private final BankUaManager bankUaManager;
	private final BankByManager bankByManager;
	private final BankKzManager bankKzManager;
	private final MetalRuManager metalRuManager;

	public RateGeneralManager(ExchangeConfiguration config,
	                          BankRuManager bankRuManager,
	                          BankUaManager bankUaManager,
	                          BankByManager bankByManager,
	                          BankKzManager bankKzManager,
	                          MetalRuManager metalRuManager) {
		this.config = config;
		this.bankRuManager = bankRuManager;
		this.bankUaManager = bankUaManager;
		this.bankByManager = bankByManager;
		this.bankKzManager = bankKzManager;
		this.metalRuManager = metalRuManager;
	}

	public void commitAllRates() {
		log.info("=> Start update exchanging rates.");
		bankRuManager.commitRates(config.getBankRu(), config.getBankRuMirror());
		bankUaManager.commitRates(config.getBankUa(), config.getBankUaMirror());
		bankByManager.commitRates(config.getBankBy());
		bankKzManager.commitRates(config.getBankKz());
		metalRuManager.commitRates(config.getMetalRu(), config.getMetalRuMirror());
		log.info("=> End update exchanging rates.");
	}
}
