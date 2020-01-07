package ru.exlmoto.exchange.manager;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.manager.impl.BankByManager;
import ru.exlmoto.exchange.manager.impl.BankKzManager;
import ru.exlmoto.exchange.manager.impl.BankRuManager;
import ru.exlmoto.exchange.manager.impl.BankUaManager;
import ru.exlmoto.exchange.manager.impl.MetalRuManager;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class RateManager {
	private final ExchangeConfiguration config;

	private final BankRuManager bankRuManager;
	private final BankUaManager bankUaManager;
	private final BankByManager bankByManager;
	private final BankKzManager bankKzManager;
	private final MetalRuManager metalRuManager;

	public void commitAllRates() {
		bankRuManager.commitRates(config.getBankRu(), config.getBankRuMirror());
		bankUaManager.commitRates(config.getBankUa(), config.getBankUaMirror());
		bankByManager.commitRates(config.getBankBy());
		bankKzManager.commitRates(config.getBankKz());
		metalRuManager.commitRates(config.getMetalRu(), config.getMetalRuMirror());
	}
}
