package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.TgMarkdownGenerator;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.exchange.manager.RateManager;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Service
public class ExchangeService {
	private final RateManager manager;
	private final TgMarkdownGenerator markdownGenerator;
	private final LocalizationHelper locale;

	public ExchangeService(RateManager manager, TgMarkdownGenerator markdownGenerator, LocalizationHelper locale) {
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
