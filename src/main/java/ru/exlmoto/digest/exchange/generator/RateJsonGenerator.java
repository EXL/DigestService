package ru.exlmoto.digest.exchange.generator;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.entity.ExchangeRateEntity;
import ru.exlmoto.digest.exchange.generator.helper.GeneratorHelper;
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

	public String getJsonReport() {
		try {
			JsonObject report = new JsonObject();
			addBankRuToReport(report);
			addBankUaToReport(report);
			addBankByToReport(report);
			addBankKzToReport(report);
			addMetalRuToReport(report);
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
			field.addProperty("uah", bankRu.getUah());
			field.addProperty("byn", bankRu.getByn());
			field.addProperty("kzt", bankRu.getKzt());
			report.add("bank_ru", field);
		});
	}

	private void addBankUaToReport(JsonObject report) {
		service.getBankUa().ifPresent(bankUa -> {
			JsonObject field = addBankProperties(bankUa);
			field.addProperty("rub", bankUa.getRub());
			field.addProperty("byn", bankUa.getByn());
			field.addProperty("kzt", bankUa.getKzt());
			report.add("bank_ua", field);
		});
	}

	private void addBankByToReport(JsonObject report) {
		service.getBankKz().ifPresent(bankKz -> {
			JsonObject field = addBankProperties(bankKz);
			field.addProperty("rub", bankKz.getRub());
			field.addProperty("uah", bankKz.getUah());
			field.addProperty("byn", bankKz.getByn());
			report.add("bank_kz", field);
		});
	}

	private void addMetalRuToReport(JsonObject report) {
		service.getMetalRu().ifPresent(metalRu -> report.add("metal_ru", addMetalProperties(metalRu)));
	}

	private void addBankKzToReport(JsonObject report) {
		service.getBankBy().ifPresent(bankBy -> {
			JsonObject field = addBankProperties(bankBy);
			field.addProperty("rub", bankBy.getRub());
			field.addProperty("uah", bankBy.getUah());
			field.addProperty("kzt", bankBy.getKzt());
			report.add("bank_by", field);
		});
	}

	private JsonObject addBankProperties(ExchangeRateEntity rate) {
		JsonObject field = new JsonObject();
		BigDecimal usd = rate.getUsd();
		field.addProperty("diff", helper.getDifference(rate.getPrev(), usd));
		field.addProperty("usd", usd);
		field.addProperty("eur", rate.getEur());
		field.addProperty("cny", rate.getCny());
		field.addProperty("gbp", rate.getGbp());
		return field;
	}

	private JsonObject addMetalProperties(ExchangeRateEntity rate) {
		JsonObject field = new JsonObject();
		BigDecimal gold = rate.getGold();
		field.addProperty("diff", helper.getDifference(rate.getPrev(), gold));
		field.addProperty("gold", gold);
		field.addProperty("silver", rate.getSilver());
		field.addProperty("platinum", rate.getPlatinum());
		field.addProperty("palladium", rate.getPalladium());
		return field;
	}
}
