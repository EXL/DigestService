package ru.exlmoto.exchange.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.configuration.ExchangeConfiguration;

@Service
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class ExchangeService {
	private final ExchangeConfiguration exchangeConfiguration;

	public ExchangeService(ExchangeConfiguration exchangeConfiguration) {
		this.exchangeConfiguration = exchangeConfiguration;
	}

	public String message() {
		return exchangeConfiguration.getBankBy();
	}
}
