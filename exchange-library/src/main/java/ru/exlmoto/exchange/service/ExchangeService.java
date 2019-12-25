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
		if (!new BankRu().process(exchangeConfiguration.getBankRu())) {
			new BankRu().process(exchangeConfiguration.getBankRuMirror());
		}
		if (!new BankUa().process(exchangeConfiguration.getBankUa())) {
			new BankUaMirror().process(exchangeConfiguration.getBankUaMirror());
		}
		new BankBy().process(exchangeConfiguration.getBankBy());
		new BankKz().process(exchangeConfiguration.getBankKz());
		if (!new MetalRu().process(exchangeConfiguration.getMetalRu())) {
			new MetalRuMirror().process(exchangeConfiguration.getMetalRuMirror());
		}
	}

	private void testAllRates() {
		new BankRu().process(exchangeConfiguration.getBankRu());
		new BankRu().process(exchangeConfiguration.getBankRuMirror());
		new BankUa().process(exchangeConfiguration.getBankUa());
		new BankUaMirror().process(exchangeConfiguration.getBankUaMirror());
		new BankBy().process(exchangeConfiguration.getBankBy());
		new BankKz().process(exchangeConfiguration.getBankKz());
		new MetalRu().process(exchangeConfiguration.getMetalRu());
		new MetalRuMirror().process(exchangeConfiguration.getMetalRuMirror());
	}

	public String message() {
		testAllRates();
		return "Hello, World!";
	}
}
