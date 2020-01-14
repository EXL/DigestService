package ru.exlmoto.digest.exchange.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.BankRuEntity;
import ru.exlmoto.digest.entity.BankUaEntity;
import ru.exlmoto.digest.entity.BankByEntity;
import ru.exlmoto.digest.entity.BankKzEntity;
import ru.exlmoto.digest.entity.MetalRuEntity;

import ru.exlmoto.digest.exchange.generator.helper.GeneratorHelper;
import ru.exlmoto.digest.exchange.generator.helper.RepositoryHelper;
import ru.exlmoto.digest.i18n.LocalizationService;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TgMarkdownGenerator {
	private final GeneratorHelper helper;
	private final RepositoryHelper repos;
	private final LocalizationService locale;

	public String bankRuReport() {
		return Optional.ofNullable(repos.getBankRu()).map(this::bankRuReportAux).orElse(errorReport());
	}

	public String bankUaReport() {
		return Optional.ofNullable(repos.getBankUa()).map(this::bankUaReportAux).orElse(errorReport());
	}

	public String bankByReport() {
		return Optional.ofNullable(repos.getBankBy()).map(this::bankByReportAux).orElse(errorReport());
	}

	public String bankKzReport() {
		return Optional.ofNullable(repos.getBankKz()).map(this::bankKzReportAux).orElse(errorReport());
	}

	public String metalRuReport() {
		return Optional.ofNullable(repos.getMetalRu()).map(this::metalRuReportAux).orElse(errorReport());
	}

	private String bankRuReportAux(BankRuEntity bankRuEntity) {
		String report = generalData(
			locale.i18n("exchange.bank.ru"), "RUB", bankRuEntity.getDate(),
			bankRuEntity.getUsd(), bankRuEntity.getEur(), bankRuEntity.getGbp(), bankRuEntity.getPrev()
		);
		report += String.format("1 UAH = %s RUB.\n", filterValue(bankRuEntity.getUah()));
		report += String.format("1 BYN = %s RUB.\n", filterValue(bankRuEntity.getByn()));
		report += String.format("1 KZT = %s RUB.\n", filterValue(bankRuEntity.getKzt()));
		return report + "```";
	}

	private String bankUaReportAux(BankUaEntity bankUaEntity) {
		String report = generalData(
			locale.i18n("exchange.bank.ua"), "UAH", bankUaEntity.getDate(),
			bankUaEntity.getUsd(), bankUaEntity.getEur(), bankUaEntity.getGbp(), bankUaEntity.getPrev()
		);
		report += String.format("1 RUB = %s UAH.\n", filterValue(bankUaEntity.getRub()));
		report += String.format("1 BYN = %s UAH.\n", filterValue(bankUaEntity.getByn()));
		report += String.format("1 KZT = %s UAH.\n", filterValue(bankUaEntity.getKzt()));
		return report + "```";
	}

	private String bankByReportAux(BankByEntity bankByEntity) {
		String report = generalData(
			locale.i18n("exchange.bank.by"), "BYN", bankByEntity.getDate(),
			bankByEntity.getUsd(), bankByEntity.getEur(), bankByEntity.getGbp(), bankByEntity.getPrev()
		);
		report += String.format("1 RUB = %s BYN.\n", filterValue(bankByEntity.getRub()));
		report += String.format("1 UAH = %s BYN.\n", filterValue(bankByEntity.getUah()));
		report += String.format("1 KZT = %s BYN.\n", filterValue(bankByEntity.getKzt()));
		return report + "```";
	}

	private String bankKzReportAux(BankKzEntity bankKzEntity) {
		String report = generalData(
			locale.i18n("exchange.bank.kz"), "KZT", bankKzEntity.getDate(),
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
		String difference = filterDifference(prev, usd);
		if (difference != null) {
			general += " " + locale.i18n("exchange.change") + " " + difference;
		}
		general += "\n" + String.format(locale.i18n("exchange.bank.header"), filterDate(date));
		general += "\n```\n";
		general += String.format("1 USD = %s %s.\n", filterValue(usd), currency);
		general += String.format("1 EUR = %s %s.\n", filterValue(eur), currency);
		general += String.format("1 GBP = %s %s.\n", filterValue(gbp), currency);
		return general;
	}

	private String metalRuReportAux(MetalRuEntity metalRuEntity) {
		String report = locale.i18n("exchange.bank.ru");
		String difference = filterDifference(metalRuEntity.getPrev(), metalRuEntity.getGold());
		if (difference != null) {
			report += " " + locale.i18n("exchange.change") + " " + difference;
		}
		report += "\n" + String.format(locale.i18n("exchange.metal.header"), filterDate(metalRuEntity.getDate()));
		report += "\n```\n";
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.gold")),
			filterValue(metalRuEntity.getGold()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.silver")),
			filterValue(metalRuEntity.getSilver()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.platinum")),
			filterValue(metalRuEntity.getPlatinum()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.palladium")),
			filterValue(metalRuEntity.getPalladium()));
		return report + "```";
	}

	private String filterDifference(BigDecimal prev, BigDecimal current) {
		String difference = helper.getDifference(prev, current);
		if (difference != null) {
			return difference.startsWith("-") ?
					difference + " " + locale.i18n("exchange.change.down") :
					"+" + difference + " " + locale.i18n("exchange.change.up");
		}
		return null;
	}

	private String filterValue(BigDecimal value) {
		return (value != null && value.signum() != -1) ?
			helper.normalizeValue(value) : locale.i18n("exchange.error.value");
	}

	private String filterMetalName(String name) {
		return helper.addTrailingSigns(name, " ", 10);
	}

	private String filterDate(String date) {
		return helper.isDateNotEmpty(date) ? date : "`" + locale.i18n("exchange.error.value") + "`";
	}

	private String errorReport() {
		return locale.i18n("exchange.error.report");
	}
}
