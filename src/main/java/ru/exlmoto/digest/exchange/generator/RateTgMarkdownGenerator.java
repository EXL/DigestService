/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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
import ru.exlmoto.digest.entity.ExchangeRateRbcEntity;
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
				case bitcoin:
					return service.getBitcoin().map(this::bitcoinReport).orElse(errorReport());
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
		report += "```" + "\n";

		report += bankRuRbcReport() + "\n";
		report += bankRuAliReport() + "\n";

		return report;
	}

	private String bankRuRbcReport() {
		String report = "";
		ExchangeRateRbcEntity usdExchEntity = service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_USD_EXCH).orElse(null);
		if (usdExchEntity != null) {
			report += String.format(locale.i18n("exchange.rbc.header"), usdExchEntity.getDate()) + "\n";
			report += "```\n";
//			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_USD_CASH).orElse(null),
//				locale.i18n("exchange.rbc.usd.cash"), false);
//			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_EUR_CASH).orElse(null),
//				locale.i18n("exchange.rbc.eur.cash"), false);
			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_USD_EXCH).orElse(null),
				locale.i18n("exchange.rbc.usd.exch"), true);
			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_EUR_EXCH).orElse(null),
				locale.i18n("exchange.rbc.eur.exch"), true);
			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_EUR_USD).orElse(null),
				locale.i18n("exchange.rbc.usd.eur"), true);
			report += bankRuRbcReportAux(service.getRbcQuotes(ExchangeRateRbcEntity.RBC_ROW_BTC_USD).orElse(null),
				locale.i18n("exchange.rbc.usd.btc"), true);
			report += "```";
		}
		return report;
	}

	private String bankRuRbcReportAux(ExchangeRateRbcEntity entity, String label, boolean diff) {
		String report = "";
		if (entity != null) {
			report += label + " " + helper.addLeadingSigns(entity.getSale(), " ", 8) +
				((diff) ? filterStringDifference(entity.getDifference(), 7) : ",  " + entity.getPurchase()) + "\n";
		}
		return report;
	}

	private String bankRuAliReport() {
		final int MAX_ROW_SIZE = 5;
		String report = locale.i18n("exchange.aliexpress.header") + "\n";

		report += "```\n";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < MAX_ROW_SIZE; ++i) {
			service.getLastAliRow(i + 1).ifPresent(
				entity -> sb.append(entity.getDate()).append(": ").append(entity.getValue()).append(" RUB.\n")
			);
		}
		report += sb.toString();
		report += "```";

		return report;
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

	private String bitcoinReport(ExchangeRateEntity bitcoinEntity) {
		String report = String.format(locale.i18n("exchange.bitcoin"), "1 BTC");

		BigDecimal usd = bitcoinEntity.getUsd();
		BigDecimal eur = bitcoinEntity.getEur();
		BigDecimal cny = bitcoinEntity.getCny();
		BigDecimal gbp = bitcoinEntity.getGbp();
		BigDecimal rub = bitcoinEntity.getRub();
		BigDecimal uah = bitcoinEntity.getUah();
		BigDecimal byn = bitcoinEntity.getByn();
		BigDecimal kzt = bitcoinEntity.getKzt();

		report += "\n" + String.format(locale.i18n("exchange.bank.header"), filterDate(bitcoinEntity.getDate()));
		report += "\n```\n";
		report += String.format("USD: %s%s\n", filterBitcoin(usd),
			filterDifferenceBitcoin(bitcoinEntity.getPrevUsd(), usd));
		report += String.format("EUR: %s%s\n", filterBitcoin(eur),
			filterDifferenceBitcoin(bitcoinEntity.getPrevEur(), eur));
		report += String.format("CNY: %s%s\n", filterBitcoin(cny),
			filterDifferenceBitcoin(bitcoinEntity.getPrevCny(), cny));
		report += String.format("GBP: %s%s\n", filterBitcoin(gbp),
			filterDifferenceBitcoin(bitcoinEntity.getPrevGbp(), gbp));
		report += String.format("RUB: %s%s\n", filterBitcoin(rub),
			filterDifferenceBitcoin(bitcoinEntity.getPrevRub(), rub));
		report += String.format("UAH: %s%s\n", filterBitcoin(uah),
			filterDifferenceBitcoin(bitcoinEntity.getPrevUah(), uah));
		report += String.format("BYN: %s%s\n", filterBitcoin(byn),
			filterDifferenceBitcoin(bitcoinEntity.getPrevByn(), byn));
//		KZT is too long for report, drop it.
//		report += String.format("KZT: %s%s\n", filterValue(kzt),
//			filterDifferenceBitcoin(bitcoinEntity.getPrevKzt(), kzt));
		return report + "```";
	}

	private String filterDifference(BigDecimal prev, BigDecimal current) {
		return filterDifference(prev, current, 8);
	}

	private String filterDifferenceMetal(BigDecimal prev, BigDecimal current) {
		return filterDifference(prev, current, 10);
	}

	private String filterDifferenceBitcoin(BigDecimal prev, BigDecimal current) {
		return filterDifference(prev, current, 11);
	}

	private String filterDifference(BigDecimal prev, BigDecimal current, int limit) {
		return filterStringDifference(helper.getDifference(prev, current), limit);
	}

	protected String filterStringDifference(String difference, int limit) {
		final String SPACE = " ";
		if (difference != null && helper.isNumeric(difference)) {
			return ", " + (difference.startsWith("-") ?
				helper.addTrailingSigns(difference, SPACE, limit) +
					" " + locale.i18n("exchange.change.down") :
				helper.addTrailingSigns((difference.startsWith("+") ? difference : "+" + difference), SPACE, limit) +
					" " + locale.i18n("exchange.change.up"));
		}
		return ".";
	}

	private String filterValue(BigDecimal value) {
		return filterValueAux(value, 7);
	}

	private String filterBitcoin(BigDecimal value) {
		return filterValueAux(value, 9);
	}

	private String filterValueAux(BigDecimal value, int maxNumberSize) {
		return (value != null && value.signum() != -1) ?
			helper.normalizeValue(value, maxNumberSize) : locale.i18n("exchange.error.value");
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
