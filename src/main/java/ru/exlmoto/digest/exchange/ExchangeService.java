package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.RateJsonGenerator;
import ru.exlmoto.digest.exchange.generator.RateTgMarkdownGenerator;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.exchange.manager.ExchangeManager;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Service
public class ExchangeService {
	private final ExchangeManager manager;
	private final RateTgMarkdownGenerator markdownGenerator;
	private final RateJsonGenerator jsonGenerator;
	private final LocaleHelper locale;

	public ExchangeService(ExchangeManager manager,
	                       RateTgMarkdownGenerator markdownGenerator,
	                       RateJsonGenerator jsonGenerator,
	                       LocaleHelper locale) {
		this.manager = manager;
		this.markdownGenerator = markdownGenerator;
		this.jsonGenerator = jsonGenerator;
		this.locale = locale;
	}

	@Scheduled(cron = "${cron.exchange.rates.update}")
	public void updateAllRates() {
		manager.commitAllRates();
	}

	public String markdownReport(String key) {
		return markdownGenerator.rateReportByKey(key);
	}

	public String jsonReport() {
		return jsonGenerator.getJsonReport();
	}

	public String buttonLabel(String key) {
		switch (ExchangeKey.checkExchangeKey(key)) {
			default:
			case bank_ru: return locale.i18n("exchange.bank.ru.button");
			case bank_ua: return locale.i18n("exchange.bank.ua.button");
			case bank_by: return locale.i18n("exchange.bank.by.button");
			case bank_kz: return locale.i18n("exchange.bank.kz.button");
			case metal_ru: return locale.i18n("exchange.metal.ru.button");
		}
	}
}
