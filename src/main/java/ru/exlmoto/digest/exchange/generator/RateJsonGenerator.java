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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

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
public class RateJsonGenerator {
	private final Logger log = LoggerFactory.getLogger(RateJsonGenerator.class);

	private final DatabaseService service;
	private final GeneratorHelper helper;
	private final LocaleHelper locale;

	public RateJsonGenerator(DatabaseService service, GeneratorHelper helper, LocaleHelper locale) {
		this.service = service;
		this.helper = helper;
		this.locale = locale;
	}

	public String getJsonReport(String key) {
		try {
			JsonObject report = new JsonObject();
			if (key.startsWith("all")) {
				addBankRuToReport(report);
				addBankUaToReport(report);
				addBankByToReport(report);
				addBankKzToReport(report);
				addMetalRuToReport(report);
				addBitcoinToReport(report);
			} else {
				switch (ExchangeKey.checkExchangeKey(key)) {
					case bank_ru: { addBankRuToReport(report); break; }
					case bank_ua: { addBankUaToReport(report); break; }
					case bank_by: { addBankByToReport(report); break; }
					case bank_kz: { addBankKzToReport(report); break; }
					case metal_ru: { addMetalRuToReport(report); break; }
					case bitcoin: { addBitcoinToReport(report); break; }
				}
			}
			return new GsonBuilder().setPrettyPrinting().create().toJson(report);
		} catch (DataAccessException dae) {
			log.error("Cannot get object from database.", dae);
		}
		JsonObject error = new JsonObject();
		error.addProperty("error", locale.i18n("exchange.error.report"));
		return error.toString();
	}

	private void addBankRuToReport(JsonObject report) {
		service.getBankRu().ifPresent(bankRu -> {
			JsonObject field = addBankProperties(bankRu);

			BigDecimal uah = bankRu.getUah();
			BigDecimal byn = bankRu.getByn();
			BigDecimal kzt = bankRu.getKzt();

			field.addProperty("uah", uah.toString());
			field.addProperty("byn", byn.toString());
			field.addProperty("kzt", kzt.toString());

			field.addProperty("diff_uah", helper.getValue(helper.getDifference(bankRu.getPrevUah(), uah)));
			field.addProperty("diff_byn", helper.getValue(helper.getDifference(bankRu.getPrevByn(), byn)));
			field.addProperty("diff_kzt", helper.getValue(helper.getDifference(bankRu.getPrevKzt(), kzt)));

			report.add("bank_ru", field);
		});
	}

	private void addBankUaToReport(JsonObject report) {
		service.getBankUa().ifPresent(bankUa -> {
			JsonObject field = addBankProperties(bankUa);

			BigDecimal rub = bankUa.getRub();
			BigDecimal byn = bankUa.getByn();
			BigDecimal kzt = bankUa.getKzt();

			field.addProperty("rub", rub.toString());
			field.addProperty("byn", byn.toString());
			field.addProperty("kzt", kzt.toString());

			field.addProperty("diff_rub", helper.getValue(helper.getDifference(bankUa.getPrevRub(), rub)));
			field.addProperty("diff_byn", helper.getValue(helper.getDifference(bankUa.getPrevByn(), byn)));
			field.addProperty("diff_kzt", helper.getValue(helper.getDifference(bankUa.getPrevKzt(), kzt)));

			report.add("bank_ua", field);
		});
	}

	private void addBankByToReport(JsonObject report) {
		service.getBankBy().ifPresent(bankBy -> {
			JsonObject field = addBankProperties(bankBy);

			BigDecimal rub = bankBy.getRub();
			BigDecimal uah = bankBy.getUah();
			BigDecimal kzt = bankBy.getKzt();

			field.addProperty("rub", rub.toString());
			field.addProperty("uah", uah.toString());
			field.addProperty("kzt", kzt.toString());

			field.addProperty("diff_rub", helper.getValue(helper.getDifference(bankBy.getPrevRub(), rub)));
			field.addProperty("diff_uah", helper.getValue(helper.getDifference(bankBy.getPrevUah(), uah)));
			field.addProperty("diff_kzt", helper.getValue(helper.getDifference(bankBy.getPrevKzt(), kzt)));

			report.add("bank_by", field);
		});
	}

	private void addBankKzToReport(JsonObject report) {
		service.getBankKz().ifPresent(bankKz -> {
			JsonObject field = addBankProperties(bankKz);

			BigDecimal rub = bankKz.getRub();
			BigDecimal uah = bankKz.getUah();
			BigDecimal byn = bankKz.getByn();

			field.addProperty("rub", rub.toString());
			field.addProperty("uah", uah.toString());
			field.addProperty("byn", byn.toString());

			field.addProperty("diff_rub", helper.getValue(helper.getDifference(bankKz.getPrevRub(), rub)));
			field.addProperty("diff_uah", helper.getValue(helper.getDifference(bankKz.getPrevUah(), uah)));
			field.addProperty("diff_byn", helper.getValue(helper.getDifference(bankKz.getPrevByn(), byn)));

			report.add("bank_kz", field);
		});
	}

	private void addMetalRuToReport(JsonObject report) {
		service.getMetalRu().ifPresent(metalRu -> report.add("metal_ru", addMetalRuProperties(metalRu)));
	}

	private JsonObject addBankProperties(ExchangeRateEntity rate) {
		JsonObject field = createJsonObjectAndAddDate(rate);

		BigDecimal usd = rate.getUsd();
		BigDecimal eur = rate.getEur();
		BigDecimal cny = rate.getCny();
		BigDecimal gbp = rate.getGbp();

		field.addProperty("usd", usd.toString());
		field.addProperty("eur", eur.toString());
		field.addProperty("cny", cny.toString());
		field.addProperty("gbp", gbp.toString());

		field.addProperty("diff_usd", helper.getValue(helper.getDifference(rate.getPrevUsd(), usd)));
		field.addProperty("diff_eur", helper.getValue(helper.getDifference(rate.getPrevEur(), eur)));
		field.addProperty("diff_cny", helper.getValue(helper.getDifference(rate.getPrevCny(), cny)));
		field.addProperty("diff_gbp", helper.getValue(helper.getDifference(rate.getPrevGbp(), gbp)));

		return field;
	}

	private JsonObject addMetalRuProperties(ExchangeRateEntity rate) {
		JsonObject field = createJsonObjectAndAddDate(rate);

		BigDecimal gold = rate.getGold();
		BigDecimal silver = rate.getSilver();
		BigDecimal platinum = rate.getPlatinum();
		BigDecimal palladium = rate.getPalladium();

		field.addProperty("gold", gold.toString());
		field.addProperty("silver", silver.toString());
		field.addProperty("platinum", platinum.toString());
		field.addProperty("palladium", palladium.toString());

		field.addProperty("diff_gold",
			helper.getValue(helper.getDifference(rate.getPrevGold(), gold)));
		field.addProperty("diff_silver",
			helper.getValue(helper.getDifference(rate.getPrevSilver(), silver)));
		field.addProperty("diff_platinum",
			helper.getValue(helper.getDifference(rate.getPrevPlatinum(), platinum)));
		field.addProperty("diff_palladium",
			helper.getValue(helper.getDifference(rate.getPrevPalladium(), palladium)));

		return field;
	}

	private void addBitcoinToReport(JsonObject report) {
		service.getBitcoin().ifPresent(bitcoin -> {
			JsonObject field = addBankProperties(bitcoin);

			BigDecimal uah = bitcoin.getUah();
			BigDecimal byn = bitcoin.getByn();
			BigDecimal kzt = bitcoin.getKzt();
			BigDecimal rub = bitcoin.getRub();

			field.addProperty("rub", rub.toString());
			field.addProperty("uah", uah.toString());
			field.addProperty("byn", byn.toString());
			field.addProperty("kzt", kzt.toString());

			field.addProperty("diff_rub", helper.getValue(helper.getDifference(bitcoin.getPrevRub(), rub)));
			field.addProperty("diff_uah", helper.getValue(helper.getDifference(bitcoin.getPrevUah(), uah)));
			field.addProperty("diff_byn", helper.getValue(helper.getDifference(bitcoin.getPrevByn(), byn)));
			field.addProperty("diff_kzt", helper.getValue(helper.getDifference(bitcoin.getPrevKzt(), kzt)));

			report.add("bitcoin", field);
		});
	}

	private JsonObject createJsonObjectAndAddDate(ExchangeRateEntity rate) {
		JsonObject field = new JsonObject();
		field.addProperty("date", rate.getDate());
		return field;
	}
}
