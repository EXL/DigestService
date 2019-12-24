package ru.exlmoto.exchange.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.config.ExchangeConfig;

@Service
@EnableConfigurationProperties(ExchangeConfig.class)
public class ExchangeService {
	private final ExchangeConfig exchangeConfig;

	public ExchangeService(ExchangeConfig exchangeConfig) {
		this.exchangeConfig = exchangeConfig;
	}

	public String message() {
		return exchangeConfig.getMessage();
	}
}
