package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankUaEntity;
import ru.exlmoto.exchange.rate.Bank;
import ru.exlmoto.exchange.repository.BankUaRepository;

import java.math.BigDecimal;

@Component
public class BankUa extends Bank {
	private final Logger LOG = LoggerFactory.getLogger(BankUa.class);
	
	private final BankUaRepository repository;

	public BankUa(BankUaRepository repository) {
		this.repository = repository;
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "840");
		eur = parseValue(document, "978");
		kzt = parseValue(document, "398");
		byn = parseValue(document, "933");
		rub = parseValue(document, "643");
		gbp = parseValue(document, "826");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("r030:contains(" + valueId + ")").parent();
		return new BigDecimal(filterCommas(element.selectFirst("rate").text()));
	}

	@Override
	protected String parseDate(Document document) {
		Element element = document.selectFirst("r030:contains(840)").parent();
		return element.selectFirst("exchangedate").text();
	}

	@Override
	protected void commitParsedValues() {
		BigDecimal prevUsd = null;
		BankUaEntity bankUaEntityFromDb = repository.getBankUa();
		if (bankUaEntityFromDb != null) {
			prevUsd = bankUaEntityFromDb.getUsd();
		}
		repository.save(new BankUaEntity(date, usd, eur, kzt, byn, rub, gbp, (prevUsd == null) ? usd : prevUsd));
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, gbp
			)
		);
	}
}
