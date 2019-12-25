package ru.exlmoto.exchange.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.config.ExchangeConfig;
import ru.exlmoto.exchange.rate.impl.BankRu;
import ru.exlmoto.exchange.rate.impl.BankUa;
import ru.exlmoto.exchange.rate.impl.BankUaMirror;
import ru.exlmoto.exchange.rate.impl.BankBy;
import ru.exlmoto.exchange.rate.impl.BankKz;
import ru.exlmoto.exchange.rate.impl.MetalRu;
import ru.exlmoto.exchange.rate.impl.MetalRuMirror;

@Service
@EnableConfigurationProperties(ExchangeConfig.class)
public class ExchangeService {
	private final ExchangeConfig exchangeConfig;

	public ExchangeService(ExchangeConfig exchangeConfig) {
		this.exchangeConfig = exchangeConfig;
	}

	private void updateAllValues() {
		if (!new BankRu().process(exchangeConfig.getBankRu())) {
			new BankRu().process(exchangeConfig.getBankRuMirror());
		}
		if (!new BankUa().process(exchangeConfig.getBankUa())) {
			new BankUaMirror().process(exchangeConfig.getBankUaMirror());
		}
		new BankBy().process(exchangeConfig.getBankBy());
		new BankKz().process(exchangeConfig.getBankKz());
		if (!new MetalRu().process(exchangeConfig.getMetalRu())) {
			new MetalRuMirror().process(exchangeConfig.getMetalRuMirror());
		}
	}

	private void testAllRates() {
		new BankRu().process(exchangeConfig.getBankRu());
		new BankRu().process(exchangeConfig.getBankRuMirror());
		new BankUa().process(exchangeConfig.getBankUa());
		new BankUaMirror().process(exchangeConfig.getBankUaMirror());
		new BankBy().process(exchangeConfig.getBankBy());
		new BankKz().process(exchangeConfig.getBankKz());
		new MetalRu().process(exchangeConfig.getMetalRu());
		new MetalRuMirror().process(exchangeConfig.getMetalRuMirror());
	}

	public String message() {
		testAllRates();
		return "Hello, World!";
	}
}
