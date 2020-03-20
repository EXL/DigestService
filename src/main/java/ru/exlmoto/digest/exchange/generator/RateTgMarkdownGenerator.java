package ru.exlmoto.digest.exchange.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.generator.helper.GeneratorHelper;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.math.BigDecimal;

@Component
public class RateTgMarkdownGenerator {
	private final Logger log = LoggerFactory.getLogger(RateTgMarkdownGenerator.class);

	private final GeneratorHelper helper;
	private final DatabaseService service;
	private final LocaleHelper locale;

	public RateTgMarkdownGenerator(GeneratorHelper helper, DatabaseService service, LocaleHelper locale) {
		this.helper = helper;
		this.service = service;
		this.locale = locale;
	}

	public String rateReportByKey(String key) {
		ExchangeKey exchangeKey = ExchangeKey.checkExchangeKey(key);
		try {
			switch (exchangeKey) {
				case bank_ru:
					return service.getBankRu().map(this::bankRuReport).orElse(errorReport());
				case bank_ua:
					return service.getBankUa().map(this::bankUaReport).orElse(errorReport());
				case bank_by:
					return service.getBankBy().map(this::bankByReport).orElse(errorReport());
				case bank_kz:
					return service.getBankKz().map(this::bankKzReport).orElse(errorReport());
				case metal_ru:
					return service.getMetalRu().map(this::metalRuReport).orElse(errorReport());
			}
		} catch (DataAccessException dae) {
			log.error("Cannot get object from database.", dae);
		}
		return errorReport();
	}

	private String bankRuReport(ExchangeRateEntity bankRuEntity) {
		String report = generalData(locale.i18n("exchange.bank.ru"), "RUB", bankRuEntity);
		report += String.format("1 UAH = %s RUB.\n", filterValue(bankRuEntity.getUah()));
		report += String.format("1 BYN = %s RUB.\n", filterValue(bankRuEntity.getByn()));
		report += String.format("1 KZT = %s RUB.\n", filterValue(bankRuEntity.getKzt()));
		return report + "```";
	}

	private String bankUaReport(ExchangeRateEntity bankUaEntity) {
		String report = generalData(locale.i18n("exchange.bank.ua"), "UAH", bankUaEntity);
		report += String.format("1 RUB = %s UAH.\n", filterValue(bankUaEntity.getRub()));
		report += String.format("1 BYN = %s UAH.\n", filterValue(bankUaEntity.getByn()));
		report += String.format("1 KZT = %s UAH.\n", filterValue(bankUaEntity.getKzt()));
		return report + "```";
	}

	private String bankByReport(ExchangeRateEntity bankByEntity) {
		String report = generalData(locale.i18n("exchange.bank.by"), "BYN", bankByEntity);
		report += String.format("1 RUB = %s BYN.\n", filterValue(bankByEntity.getRub()));
		report += String.format("1 UAH = %s BYN.\n", filterValue(bankByEntity.getUah()));
		report += String.format("1 KZT = %s BYN.\n", filterValue(bankByEntity.getKzt()));
		return report + "```";
	}

	private String bankKzReport(ExchangeRateEntity bankKzEntity) {
		String report = generalData(locale.i18n("exchange.bank.kz"), "KZT", bankKzEntity);
		report += String.format("1 RUB = %s KZT.\n", filterValue(bankKzEntity.getRub()));
		report += String.format("1 UAH = %s KZT.\n", filterValue(bankKzEntity.getUah()));
		report += String.format("1 BYN = %s KZT.\n", filterValue(bankKzEntity.getByn()));
		return report + "```";
	}

	private String generalData(String header, String currency, ExchangeRateEntity entity) {
		String general = header;
		String difference = filterDifference(entity.getPrev(), entity.getUsd());
		if (difference != null) {
			general += " " + locale.i18n("exchange.change") + " " + difference;
		}
		general += "\n" + String.format(locale.i18n("exchange.bank.header"), filterDate(entity.getDate()));
		general += "\n```\n";
		general += String.format("1 USD = %s %s.\n", filterValue(entity.getUsd()), currency);
		general += String.format("1 EUR = %s %s.\n", filterValue(entity.getEur()), currency);
		general += String.format("1 CNY = %s %s.\n", filterValue(entity.getCny()), currency);
		general += String.format("1 GBP = %s %s.\n", filterValue(entity.getGbp()), currency);
		return general;
	}

	private String metalRuReport(ExchangeRateEntity metalRuEntity) {
		String report = locale.i18n("exchange.bank.ru");
		String difference = filterDifference(metalRuEntity.getPrev(), metalRuEntity.getGold());
		if (difference != null) {
			report += " " + locale.i18n("exchange.change") + " " + difference;
		}
		report += "\n" + String.format(locale.i18n("exchange.metal.ru.header"),
			filterDate(metalRuEntity.getDate()));
		report += "\n```\n";
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.ru.gold")),
			filterValue(metalRuEntity.getGold()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.ru.silver")),
			filterValue(metalRuEntity.getSilver()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.ru.platinum")),
			filterValue(metalRuEntity.getPlatinum()));
		report += String.format("%s %s RUB.\n", filterMetalName(locale.i18n("exchange.metal.ru.palladium")),
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
