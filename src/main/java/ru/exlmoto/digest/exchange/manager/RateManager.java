package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
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
		commitBankRu(config.getBankRu(), config.getBankRuMirror());
		commitBankUa(config.getBankUa(), config.getBankUaMirror());
		commitBankBy(config.getBankBy());
		commitBankKz(config.getBankKz());
		commitMetalRu(config.getMetalRu(), config.getMetalRuMirror());
		log.info("=> End update exchanging rates.");
	}

	public void commitBankRu(String url, String mirror) {
		new BankRuParser().commitRates(url, mirror, repository, rest);
	}

	public void commitBankUa(String url, String mirror) {
		new BankUaParser().commitRates(url, mirror, repository, rest);
	}

	public void commitBankBy(String url) {
		new BankByParser().commitRates(url, repository, rest);
	}

	public void commitBankKz(String url) {
		new BankKzParser().commitRates(url, repository, rest);
	}

	public void commitMetalRu(String url, String mirror) {
		new MetalRuParser().commitRates(url, mirror, repository, rest);
	}
}
