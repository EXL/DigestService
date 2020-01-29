package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.exchange.parser.impl.*;
import ru.exlmoto.digest.repository.RateRepository;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class RateManager {
	private final Logger log = LoggerFactory.getLogger(RateManager.class);

	private final ExchangeConfiguration config;
	private final RateRepository repository;
	private final RestHelper rest;

	public RateManager(ExchangeConfiguration config, RateRepository repository, RestHelper rest) {
		this.config = config;
		this.repository = repository;
		this.rest = rest;
	}

	public void commitAllRates() {
		log.info("=> Start update exchanging rates.");
		new BankRuParser().commitRates(config.getBankRu(), config.getBankRuMirror(), repository, rest);
		new BankUaParser().commitRates(config.getBankUa(), config.getBankUaMirror(), repository, rest);
		new BankByParser().commitRates(config.getBankBy(), repository, rest);
		new BankKzParser().commitRates(config.getBankKz(), repository, rest);
		new MetalRuParser().commitRates(config.getMetalRu(), config.getMetalRuMirror(), repository, rest);
		log.info("=> End update exchanging rates.");
	}
}
