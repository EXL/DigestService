package ru.exlmoto.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final Logger LOG = LoggerFactory.getLogger(ExchangeService.class);

	private final ExchangeConfiguration exchangeConfiguration;

	private final BankRu bankRu;
	private final BankUa bankUa;
	private final BankUaMirror bankUaMirror;
	private final BankBy bankBy;
	private final BankKz bankKz;
	private final MetalRu metalRu;
	private final MetalRuMirror metalRuMirror;

	public ExchangeService(ExchangeConfiguration exchangeConfiguration,
	                      BankRu bankRu,
	                      BankUa bankUa,
	                      BankUaMirror bankUaMirror,
	                      BankBy bankBy,
	                      BankKz bankKz,
	                      MetalRu metalRu,
	                      MetalRuMirror metalRuMirror) {
		this.exchangeConfiguration = exchangeConfiguration;
		this.bankRu = bankRu;
		this.bankUa = bankUa;
		this.bankUaMirror = bankUaMirror;
		this.bankBy = bankBy;
		this.bankKz = bankKz;
		this.metalRu = metalRu;
		this.metalRuMirror = metalRuMirror;
	}

	public void updateAllRates() {
		LOG.info("=> Start update exchanging rates.");
		if (!bankRu.process(exchangeConfiguration.getBankRu())) {
			LOG.info("==> Using BankRuMirror.");
			bankRu.process(exchangeConfiguration.getBankRuMirror());
		}
		if (!bankUa.process(exchangeConfiguration.getBankUa())) {
			LOG.info("==> Using BankUaMirror.");
			bankUaMirror.process(exchangeConfiguration.getBankUaMirror());
		}
		bankBy.process(exchangeConfiguration.getBankBy());
		bankKz.process(exchangeConfiguration.getBankKz());
		if (!metalRu.process(exchangeConfiguration.getMetalRu())) {
			LOG.info("==> Using MetalRuMirror.");
			metalRuMirror.process(exchangeConfiguration.getMetalRuMirror());
		}
		LOG.info("=> End update exchanging rates.");
	}

	public void testAllSources() {
		bankRu.process(exchangeConfiguration.getBankRu());
		bankRu.process(exchangeConfiguration.getBankRuMirror());
		bankUa.process(exchangeConfiguration.getBankUa());
		bankUaMirror.process(exchangeConfiguration.getBankUaMirror());
		bankBy.process(exchangeConfiguration.getBankBy());
		bankKz.process(exchangeConfiguration.getBankKz());
		metalRu.process(exchangeConfiguration.getMetalRu());
		metalRuMirror.process(exchangeConfiguration.getMetalRuMirror());
	}
}
