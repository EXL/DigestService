/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

		BigDecimal uah = bankRuEntity.getUah();
		BigDecimal byn = bankRuEntity.getByn();
		BigDecimal kzt = bankRuEntity.getKzt();

		report += String.format("1 UAH: %s%s\n", filterValue(uah),
			filterDifference(bankRuEntity.getPrevUah(), uah));
		report += String.format("1 BYN: %s%s\n", filterValue(byn),
			filterDifference(bankRuEntity.getPrevByn(), byn));
		report += String.format("1 KZT: %s%s\n", filterValue(kzt),
			filterDifference(bankRuEntity.getPrevKzt(), kzt));
		return report + "```";
	}

	private String bankUaReport(ExchangeRateEntity bankUaEntity) {
		String report = generalData(locale.i18n("exchange.bank.ua"), "UAH", bankUaEntity);

		BigDecimal rub = bankUaEntity.getRub();
		BigDecimal byn = bankUaEntity.getByn();
		BigDecimal kzt = bankUaEntity.getKzt();

		report += String.format("1 RUB: %s%s\n", filterValue(rub),
			filterDifference(bankUaEntity.getPrevRub(), rub));
		report += String.format("1 BYN: %s%s\n", filterValue(byn),
			filterDifference(bankUaEntity.getPrevByn(), byn));
		report += String.format("1 KZT: %s%s\n", filterValue(kzt),
			filterDifference(bankUaEntity.getPrevKzt(), kzt));
		return report + "```";
	}

	private String bankByReport(ExchangeRateEntity bankByEntity) {
		String report = generalData(locale.i18n("exchange.bank.by"), "BYN", bankByEntity);

		BigDecimal rub = bankByEntity.getRub();
		BigDecimal uah = bankByEntity.getUah();
		BigDecimal kzt = bankByEntity.getKzt();

		report += String.format("1 RUB: %s%s\n", filterValue(rub),
			filterDifference(bankByEntity.getPrevRub(), rub));
		report += String.format("1 UAH: %s%s\n", filterValue(uah),
			filterDifference(bankByEntity.getPrevUah(), uah));
		report += String.format("1 KZT: %s%s\n", filterValue(kzt),
			filterDifference(bankByEntity.getPrevKzt(), kzt));
		return report + "```";
	}

	private String bankKzReport(ExchangeRateEntity bankKzEntity) {
		String report = generalData(locale.i18n("exchange.bank.kz"), "KZT", bankKzEntity);

		BigDecimal rub = bankKzEntity.getRub();
		BigDecimal uah = bankKzEntity.getUah();
		BigDecimal byn = bankKzEntity.getByn();

		report += String.format("1 RUB: %s%s\n", filterValue(rub),
			filterDifference(bankKzEntity.getPrevRub(), rub));
		report += String.format("1 UAH: %s%s\n", filterValue(uah),
			filterDifference(bankKzEntity.getPrevUah(), uah));
		report += String.format("1 BYN: %s%s\n", filterValue(byn),
			filterDifference(bankKzEntity.getPrevByn(), byn));
		return report + "```";
	}

	private String generalData(String header, String currency, ExchangeRateEntity entity) {
		String general = String.format(header, currency);

		BigDecimal usd = entity.getUsd();
		BigDecimal eur = entity.getEur();
		BigDecimal cny = entity.getCny();
		BigDecimal gbp = entity.getGbp();

		general += "\n" + String.format(locale.i18n("exchange.bank.header"), filterDate(entity.getDate()));
		general += "\n```\n";
		general += String.format("1 USD: %s%s\n", filterValue(usd),
			filterDifference(entity.getPrevUsd(), usd));
		general += String.format("1 EUR: %s%s\n", filterValue(eur),
			filterDifference(entity.getPrevEur(), eur));
		general += String.format("1 CNY: %s%s\n", filterValue(cny),
			filterDifference(entity.getPrevCny(), cny));
		general += String.format("1 GBP: %s%s\n", filterValue(gbp),
			filterDifference(entity.getPrevGbp(), gbp));
		return general;
	}

	private String metalRuReport(ExchangeRateEntity metalRuEntity) {
		String report = String.format(locale.i18n("exchange.bank.ru"), "RUB");

		BigDecimal gold = metalRuEntity.getGold();
		BigDecimal silver = metalRuEntity.getSilver();
		BigDecimal platinum = metalRuEntity.getPlatinum();
		BigDecimal palladium = metalRuEntity.getPalladium();

		report += "\n" + String.format(locale.i18n("exchange.metal.ru.header"),
			filterDate(metalRuEntity.getDate()));
		report += "\n```\n";
		report += String.format("%s %s%s\n", filterMetalName(locale.i18n("exchange.metal.ru.gold")),
			filterValue(gold), filterDifferenceMetal(metalRuEntity.getPrevGold(), gold));
		report += String.format("%s %s%s\n", filterMetalName(locale.i18n("exchange.metal.ru.silver")),
			filterValue(silver), filterDifferenceMetal(metalRuEntity.getPrevSilver(), silver));
		report += String.format("%s %s%s\n", filterMetalName(locale.i18n("exchange.metal.ru.platinum")),
			filterValue(platinum), filterDifferenceMetal(metalRuEntity.getPrevPlatinum(), platinum));
		report += String.format("%s %s%s\n", filterMetalName(locale.i18n("exchange.metal.ru.palladium")),
			filterValue(palladium), filterDifferenceMetal(metalRuEntity.getPrevPalladium(), palladium));
		return report + "```";
	}

	private String filterDifference(BigDecimal prev, BigDecimal current) {
		return filterDifference(prev, current, false);
	}

	private String filterDifferenceMetal(BigDecimal prev, BigDecimal current) {
		return filterDifference(prev, current, true);
	}

	private String filterDifference(BigDecimal prev, BigDecimal current, boolean isLong) {
		final int LIMIT = (isLong) ? 10 : 8;
		final String SPACE = " ";

		String difference = helper.getDifference(prev, current);
		if (difference != null) {
			return ", " + (difference.startsWith("-") ?
				helper.addTrailingSigns(difference, SPACE, LIMIT) +
					" " + locale.i18n("exchange.change.down") :
				helper.addTrailingSigns("+" + difference, SPACE, LIMIT) +
					" " + locale.i18n("exchange.change.up"));
		}
		return ".";
	}

	private String filterValue(BigDecimal value) {
		return (value != null && value.signum() != -1) ?
			helper.normalizeValue(value) : locale.i18n("exchange.error.value");
	}

	private String filterMetalName(String name) {
		return helper.addTrailingSigns(name, " ", 3);
	}

	private String filterDate(String date) {
		return helper.isDateNotEmpty(date) ? date : "`" + locale.i18n("exchange.error.value") + "`";
	}

	private String errorReport() {
		return locale.i18n("exchange.error.report");
	}
}
