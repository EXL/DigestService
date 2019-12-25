package ru.exlmoto.exchange.service;

import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.worker.ExchangeWorker;

@Service
public class ExchangeService {
	private final ExchangeWorker exchangeWorker;

	public ExchangeService(ExchangeWorker exchangeWorker) {
		this.exchangeWorker = exchangeWorker;
	}

	public String message() {
		exchangeWorker.updateAllRates();
		return "Hello, World!";
	}
}
