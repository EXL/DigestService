package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.exchange.parser.impl.BankRuParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaParser;
import ru.exlmoto.digest.exchange.parser.impl.BankUaMirrorParser;
import ru.exlmoto.digest.exchange.parser.impl.BankByParser;
import ru.exlmoto.digest.exchange.parser.impl.BankKzParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuParser;
import ru.exlmoto.digest.exchange.parser.impl.MetalRuMirrorParser;
import ru.exlmoto.digest.service.RateService;
import ru.exlmoto.digest.util.rest.RestHelper;

@Component
public class ExchangeManager {
	private final Logger log = LoggerFactory.getLogger(ExchangeManager.class);

	private final ExchangeConfiguration config;
	private final RateService service;
	private final RestHelper rest;

	public ExchangeManager(ExchangeConfiguration config, RateService service, RestHelper rest) {
		this.config = config;
		this.service = service;
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
		if (!new BankRuParser().commitRates(url, service, rest)) {
			new BankRuParser().commitRatesMirror(mirror, service, rest);
		}
	}

	public void commitBankUa(String url, String mirror) {
		if (!new BankUaParser().commitRates(url, service, rest)) {
			new BankUaMirrorParser().commitRates(mirror, service, rest);
		}
	}

	public void commitBankBy(String url) {
		new BankByParser().commitRates(url, service, rest);
	}

	public void commitBankKz(String url) {
		new BankKzParser().commitRates(url, service, rest);
	}

	public void commitMetalRu(String url, String mirror) {
		if (!new MetalRuParser().commitRates(url, service, rest)) {
			new MetalRuMirrorParser().commitRates(mirror, service, rest);
		}
	}
}
