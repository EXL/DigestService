package ru.exlmoto.exchange.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.domain.BankUaEntity;
import ru.exlmoto.exchange.parser.Bank;
import ru.exlmoto.exchange.repository.BankUaRepository;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankUa extends Bank {
	private final BankUaRepository repository;

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
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, RUB: %s, GBP: %s",
				date, usd, eur, kzt, byn, rub, gbp
			)
		);
	}
}
