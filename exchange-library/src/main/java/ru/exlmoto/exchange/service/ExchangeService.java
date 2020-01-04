package ru.exlmoto.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.ExchangeConfiguration;
import ru.exlmoto.exchange.generator.MarkdownGenerator;
import ru.exlmoto.exchange.parser.impl.BankRu;
import ru.exlmoto.exchange.parser.impl.BankUa;
import ru.exlmoto.exchange.parser.impl.BankUaMirror;
import ru.exlmoto.exchange.parser.impl.BankBy;
import ru.exlmoto.exchange.parser.impl.BankKz;
import ru.exlmoto.exchange.parser.impl.MetalRu;
import ru.exlmoto.exchange.parser.impl.MetalRuMirror;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableConfigurationProperties(ExchangeConfiguration.class)
public class ExchangeService {
	private final ExchangeConfiguration exchangeConfiguration;

	private final MarkdownGenerator markdownGenerator;

	private final BankRu bankRu;
	private final BankUa bankUa;
	private final BankUaMirror bankUaMirror;
	private final BankBy bankBy;
	private final BankKz bankKz;
	private final MetalRu metalRu;
	private final MetalRuMirror metalRuMirror;

	public void updateAllRates() {
		log.info("=> Start update exchanging rates.");
		if (!bankRu.process(exchangeConfiguration.getBankRu())) {
			log.info("==> Using BankRuMirror.");
			bankRu.process(exchangeConfiguration.getBankRuMirror());
		}
		if (!bankUa.process(exchangeConfiguration.getBankUa())) {
			log.info("==> Using BankUaMirror.");
			bankUaMirror.process(exchangeConfiguration.getBankUaMirror());
		}
		bankBy.process(exchangeConfiguration.getBankBy());
		bankKz.process(exchangeConfiguration.getBankKz());
		if (!metalRu.process(exchangeConfiguration.getMetalRu())) {
			log.info("==> Using MetalRuMirror.");
			metalRuMirror.process(exchangeConfiguration.getMetalRuMirror());
		}
		log.info("=> End update exchanging rates.");
	}

	public String markdownBankRuReport() {
		return markdownGenerator.bankRuReport();
	}

	public String markdownBankUaReport() {
		return markdownGenerator.bankUaReport();
	}

	public String markdownBankByReport() {
		return markdownGenerator.bankByReport();
	}

	public String markdownBankKzReport() {
		return markdownGenerator.bankKzReport();
	}

	public String markdownMetalRuReport() {
		return markdownGenerator.metalRuReport();
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
