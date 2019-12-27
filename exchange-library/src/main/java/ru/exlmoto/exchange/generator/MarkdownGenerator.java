package ru.exlmoto.exchange.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.domain.BankByEntity;
import ru.exlmoto.exchange.domain.BankKzEntity;
import ru.exlmoto.exchange.domain.BankRuEntity;
import ru.exlmoto.exchange.domain.BankUaEntity;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.repository.BankKzRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

import java.math.BigDecimal;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MarkdownGenerator {
	@Value("${general.lang}")
	private final String langTag;
	private final MessageSource messageSource;

	protected final BankRuRepository bankRuRepository;
	protected final BankUaRepository bankUaRepository;
	protected final BankByRepository bankByRepository;
	protected final BankKzRepository bankKzRepository;
	protected final MetalRuRepository metalRuRepository;

	public String bankRuReport() {
		BankRuEntity bankRuEntity = bankRuRepository.getBankRu();
		String report = generalData(
			i18n("bank.ru"), "RUB", bankRuEntity.getDate(),
			bankRuEntity.getUsd(), bankRuEntity.getEur(), bankRuEntity.getGbp(), bankRuEntity.getPrev()
		);
		report += String.format("1 UAH = %s RUB.\n", bankRuEntity.getUah());
		report += String.format("1 BYN = %s RUB.\n", bankRuEntity.getByn());
		report += String.format("1 KZT = %s RUB.\n", bankRuEntity.getKzt());
		return report + "```";
	}

	public String bankUaReport() {
		BankUaEntity bankUaEntity = bankUaRepository.getBankUa();
		String report = generalData(
			i18n("bank.ua"), "UAH", bankUaEntity.getDate(),
			bankUaEntity.getUsd(), bankUaEntity.getEur(), bankUaEntity.getGbp(), bankUaEntity.getPrev()
		);
		report += String.format("1 RUB = %s UAH.\n", bankUaEntity.getRub());
		report += String.format("1 BYN = %s UAH.\n", bankUaEntity.getByn());
		report += String.format("1 KZT = %s UAH.\n", bankUaEntity.getKzt());
		return report + "```";
	}

	public String bankByReport() {
		BankByEntity bankByEntity = bankByRepository.getBankBy();
		String report = generalData(
			i18n("bank.by"), "BYN", bankByEntity.getDate(),
			bankByEntity.getUsd(), bankByEntity.getEur(), bankByEntity.getGbp(), bankByEntity.getPrev()
		);
		report += String.format("1 RUB = %s BYN.\n", bankByEntity.getRub());
		report += String.format("1 UAH = %s BYN.\n", bankByEntity.getUah());
		report += String.format("1 KZT = %s BYN.\n", bankByEntity.getKzt());
		return report + "```";
	}

	public String bankKzReport() {
		BankKzEntity bankKzEntity = bankKzRepository.getBankKz();
		String report = generalData(
			i18n("bank.kz"), "KZT", bankKzEntity.getDate(),
			bankKzEntity.getUsd(), bankKzEntity.getEur(), bankKzEntity.getGbp(), bankKzEntity.getPrev()
		);
		report += String.format("1 RUB = %s KZT.\n", bankKzEntity.getRub());
		report += String.format("1 UAH = %s KZT.\n", bankKzEntity.getUah());
		report += String.format("1 BYN = %s KZT.\n", bankKzEntity.getByn());
		return report + "```";
	}

	private String generalData(String header, String currency, String date,
	                           BigDecimal usd, BigDecimal eur, BigDecimal gbp, BigDecimal prev) {
		String general = header;
		if (true) {
			general += " " + i18n("change") + " " + prev;
		}
		general += "\n" + String.format(i18n("header"), date);
		general += "\n```\n";
		general += String.format("1 USD = %s %s.\n", usd, currency);
		general += String.format("1 EUR = %s %s.\n", eur, currency);
		general += String.format("1 GBP = %s %s.\n", gbp, currency);
		return general;
	}

	private String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}
}
