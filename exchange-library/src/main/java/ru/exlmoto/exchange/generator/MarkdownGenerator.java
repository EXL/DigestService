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
	private String langTag;

	private final MessageSource messageSource;

	private final BankRuRepository bankRuRepository;
	private final BankUaRepository bankUaRepository;
	private final BankByRepository bankByRepository;
	private final BankKzRepository bankKzRepository;
	private final MetalRuRepository metalRuRepository;

	public String bankRuReport() {
		BankRuEntity bankRuEntity = bankRuRepository.getBankRu();
		return (bankRuEntity.checkAllValues()) ? bankRuReportAux(bankRuEntity) : i18n("error.report");
	}

	private String bankRuReportAux(BankRuEntity bankRuEntity) {
		String report = generalData(
			i18n("bank.ru"), "RUB", bankRuEntity.getDate(),
			bankRuEntity.getUsd(), bankRuEntity.getEur(), bankRuEntity.getGbp(), bankRuEntity.getPrev()
		);
		report += String.format("1 UAH = %s RUB.\n", filterValue(bankRuEntity.getUah()));
		report += String.format("1 BYN = %s RUB.\n", filterValue(bankRuEntity.getByn()));
		report += String.format("1 KZT = %s RUB.\n", filterValue(bankRuEntity.getKzt()));
		return report + "```";
	}

	public String bankUaReport() {
		BankUaEntity bankUaEntity = bankUaRepository.getBankUa();
		return bankUaEntity.checkAllValues() ? bankUaReportAux(bankUaEntity) : i18n("error.report");
	}

	private String bankUaReportAux(BankUaEntity bankUaEntity) {
		String report = generalData(
			i18n("bank.ua"), "UAH", bankUaEntity.getDate(),
			bankUaEntity.getUsd(), bankUaEntity.getEur(), bankUaEntity.getGbp(), bankUaEntity.getPrev()
		);
		report += String.format("1 RUB = %s UAH.\n", filterValue(bankUaEntity.getRub()));
		report += String.format("1 BYN = %s UAH.\n", filterValue(bankUaEntity.getByn()));
		report += String.format("1 KZT = %s UAH.\n", filterValue(bankUaEntity.getKzt()));
		return report + "```";
	}

	public String bankByReport() {
		BankByEntity bankByEntity = bankByRepository.getBankBy();
		return bankByEntity.checkAllValues() ? bankByReportAux(bankByEntity) : i18n("error.report");
	}

	public String bankByReportAux(BankByEntity bankByEntity) {
		String report = generalData(
			i18n("bank.by"), "BYN", bankByEntity.getDate(),
			bankByEntity.getUsd(), bankByEntity.getEur(), bankByEntity.getGbp(), bankByEntity.getPrev()
		);
		report += String.format("1 RUB = %s BYN.\n", filterValue(bankByEntity.getRub()));
		report += String.format("1 UAH = %s BYN.\n", filterValue(bankByEntity.getUah()));
		report += String.format("1 KZT = %s BYN.\n", filterValue(bankByEntity.getKzt()));
		return report + "```";
	}

	public String bankKzReport() {
		BankKzEntity bankKzEntity = bankKzRepository.getBankKz();
		return bankKzEntity.checkAllValues() ? bankKzReportAux(bankKzEntity) : i18n("error.report");
	}

	public String bankKzReportAux(BankKzEntity bankKzEntity) {
		String report = generalData(
			i18n("bank.kz"), "KZT", bankKzEntity.getDate(),
			bankKzEntity.getUsd(), bankKzEntity.getEur(), bankKzEntity.getGbp(), bankKzEntity.getPrev()
		);
		report += String.format("1 RUB = %s KZT.\n", filterValue(bankKzEntity.getRub()));
		report += String.format("1 UAH = %s KZT.\n", filterValue(bankKzEntity.getUah()));
		report += String.format("1 BYN = %s KZT.\n", filterValue(bankKzEntity.getByn()));
		return report + "```";
	}

	private String generalData(String header, String currency, String date,
	                           BigDecimal usd, BigDecimal eur, BigDecimal gbp, BigDecimal prev) {
		String general = header;
		String difference = getDifference(prev, usd);
		if (difference != null) {
			general += " " + i18n("change") + " " + difference;
		}
		general += "\n" + String.format(i18n("header"), filterDate(date));
		general += "\n```\n";
		general += String.format("1 USD = %s %s.\n", filterValue(usd), currency);
		general += String.format("1 EUR = %s %s.\n", filterValue(eur), currency);
		general += String.format("1 GBP = %s %s.\n", filterValue(gbp), currency);
		return general;
	}

	private String getDifference(BigDecimal prev, BigDecimal current) {
		if (prev == null || current == null) {
			return null;
		}
		if ((prev.compareTo(BigDecimal.ZERO) == 0) || (current.compareTo(BigDecimal.ZERO) == 0)) {
			return null;
		}
		if (prev.compareTo(current) == 0) {
			return null;
		}
		return getDifferenceSign(prev.subtract(current));
	}

	private String getDifferenceSign(BigDecimal difference) {
		return (difference.compareTo(BigDecimal.ZERO) < 0) ?
			String.format("%.4f", difference) + " " + i18n("change.down") :
			"+" + String.format("%.4f", difference) + " " + i18n("change.up");
	}

	private String filterValue(BigDecimal value) {
		if (value == null) {
			return i18n("error.value");
		}

		return value.toString();
	}

	private String filterDate(String date) {
		return (date == null || date.isEmpty() || date.equals("null")) ? "`" + i18n("error.value") + "`" : date;
	}

	private String i18n(String key) {
		return messageSource.getMessage(key, null, Locale.forLanguageTag(langTag));
	}
}
