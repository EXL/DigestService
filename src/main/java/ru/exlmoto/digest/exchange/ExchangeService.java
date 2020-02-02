package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.ExchangeTgMarkdownGenerator;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.exchange.manager.ExchangeManager;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Service
public class ExchangeService {
	private final ExchangeManager manager;
	private final ExchangeTgMarkdownGenerator markdownGenerator;
	private final LocalizationHelper locale;

	public ExchangeService(ExchangeManager manager,
	                       ExchangeTgMarkdownGenerator markdownGenerator,
	                       LocalizationHelper locale) {
		this.manager = manager;
		this.markdownGenerator = markdownGenerator;
		this.locale = locale;
	}

	@Scheduled(cron = "${cron.exchange.rates.update}")
	public void updateAllRates() {
		manager.commitAllRates();
	}

	public String markdownReport(String key) {
		return markdownGenerator.rateReportByKey(key);
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
