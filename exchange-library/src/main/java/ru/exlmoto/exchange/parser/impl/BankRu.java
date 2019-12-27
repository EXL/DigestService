package ru.exlmoto.exchange.parser.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.domain.BankRuEntity;
import ru.exlmoto.exchange.parser.Bank;
import ru.exlmoto.exchange.repository.BankRuRepository;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class BankRu extends Bank {
	private final BankRuRepository repository;

	@Override
	protected void parseDocumentAux(Document document) {
		usd = parseValue(document, "R01235");
		eur = parseValue(document, "R01239");
		kzt = parseValue(document, "R01335");
		byn = parseValue(document, "R01090B");
		uah = parseValue(document, "R01720");
		gbp = parseValue(document, "R01035");
	}

	@Override
	protected BigDecimal parseValueAux(Document document, String valueId) {
		Element element = document.selectFirst("Valute[ID=" + valueId + "]");
		BigDecimal nominal = new BigDecimal(filterCommas(element.selectFirst("Nominal").text()));
		BigDecimal value = new BigDecimal(filterCommas(element.selectFirst("Value").text()));
		return value.divide(nominal, BigDecimal.ROUND_FLOOR);
	}

	@Override
	protected String parseDate(Document document) {
		return document.selectFirst("ValCurs").attr("Date");
	}

	@Override
	protected void commitParsedValues() {
		BigDecimal prevUsd = null;
		BankRuEntity bankRuEntityFromDb = repository.getBankRu();
		if (bankRuEntityFromDb != null) {
			prevUsd = bankRuEntityFromDb.getUsd();
		}
		repository.save(new BankRuEntity(date, usd, eur, kzt, byn, uah, gbp, (prevUsd == null) ? usd : prevUsd));
	}

	@Override
	protected void logParsedValues() {
		log.info(String.format(
				"===> Date: %s, USD: %s, EUR: %s, KZT: %s, BYN: %s, UAH: %s, GBP: %s",
				date, usd, eur, kzt, byn, uah, gbp
			)
		);
	}
}
