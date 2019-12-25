package ru.exlmoto.exchange.rate.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.entity.BankByEntity;
import ru.exlmoto.exchange.rate.Bank;
import ru.exlmoto.exchange.repository.BankByRepository;

import java.math.BigDecimal;

@Component
public class BankBy extends Bank {
	private final Logger LOG = LoggerFactory.getLogger(BankBy.class);

	private final BankByRepository repository;

	public BankBy(BankByRepository repository) {
		this.repository = repository;
	}

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "145");
		eur = parseValue(document, "292");
		kzt = parseValue(document, "301");
		rub = parseValue(document, "298");
		uah = parseValue(document, "290");
		gbp = parseValue(document, "143");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Currency[Id=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Scale").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Rate").text()));
		return value.divide(nominal, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("DailyExRates").attr("Date");
	}

	@Override
	protected void commitParsedValues() {
		BigDecimal prevUsd = null;
		BankByEntity bankByEntityFromDb = repository.getBankBy();
		if (bankByEntityFromDb != null) {
			prevUsd = bankByEntityFromDb.getUsd();
		}
		BankByEntity bankByEntity = new BankByEntity();
		bankByEntity.determineAll(date, usd, eur, kzt, rub, uah, gbp, (prevUsd == null) ? usd : prevUsd);
		repository.save(bankByEntity);
	}

	@Override
	protected void logParsedValues() {
		LOG.info(String.format(
				"Date: %s, USD: %s, EUR: %s, KZT: %s, RUB: %s, UAH: %s, GBP: %s",
				date, usd, eur, kzt, rub, uah, gbp
			)
		);
	}
}
