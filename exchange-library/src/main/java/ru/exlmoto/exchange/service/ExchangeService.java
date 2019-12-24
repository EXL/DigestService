package ru.exlmoto.exchange.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.exchange.rate.impl.BankRu;
import ru.exlmoto.exchange.rate.impl.BankUa;
import ru.exlmoto.exchange.rate.impl.BankUaMirror;
import ru.exlmoto.exchange.rate.impl.BankBy;
import ru.exlmoto.exchange.rate.impl.BankKz;
import ru.exlmoto.exchange.rate.impl.MetalRu;
import ru.exlmoto.exchange.rate.impl.MetalRuMirror;

@Service
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class ExchangeService {
	private final ExchangeConfiguration exchangeConfiguration;

	public ExchangeService(ExchangeConfiguration exchangeConfiguration) {
		this.exchangeConfiguration = exchangeConfiguration;
	}

	private void updateAllValues() {
		boolean rest = exchangeConfiguration.useSpringRestTemplate();
		if (!new BankRu().process(exchangeConfiguration.getBankRu(), rest)) {
			new BankRu().process(exchangeConfiguration.getBankRuMirror(), rest);
		}
		if (!new BankUa().process(exchangeConfiguration.getBankUa(), rest)) {
			new BankUaMirror().process(exchangeConfiguration.getBankUaMirror(), rest);
		}
		new BankBy().process(exchangeConfiguration.getBankBy(), rest);
		new BankKz().process(exchangeConfiguration.getBankKz(), rest);
		if (!new MetalRu().process(exchangeConfiguration.getMetalRu(), rest)) {
			new MetalRuMirror().process(exchangeConfiguration.getMetalRuMirror(), rest);
		}
	}

	private void testAllRates() {
		boolean rest = exchangeConfiguration.useSpringRestTemplate();
		new BankRu().process(exchangeConfiguration.getBankRu(), rest);
		new BankRu().process(exchangeConfiguration.getBankRuMirror(), rest);
		new BankUa().process(exchangeConfiguration.getBankUa(), rest);
		new BankUaMirror().process(exchangeConfiguration.getBankUaMirror(), rest);
		new BankBy().process(exchangeConfiguration.getBankBy(), rest);
		new BankKz().process(exchangeConfiguration.getBankKz(), rest);
		new MetalRu().process(exchangeConfiguration.getMetalRu(), rest);
		new MetalRuMirror().process(exchangeConfiguration.getMetalRuMirror(), rest);
	}

	public String message() {
		testAllRates();
		return "Hello, World!";
	}
}
