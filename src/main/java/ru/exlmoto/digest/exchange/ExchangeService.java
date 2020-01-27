package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.TgMarkdownGenerator;
import ru.exlmoto.digest.exchange.key.ExchangeKey;
import ru.exlmoto.digest.exchange.manager.RateGeneralManager;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Service
public class ExchangeService {
	private final TgMarkdownGenerator markdownGenerator;
	private final RateGeneralManager rateGeneralManager;
	private final LocalizationHelper localizationHelper;

	public ExchangeService(TgMarkdownGenerator markdownGenerator,
	                       RateGeneralManager rateGeneralManager,
	                       LocalizationHelper localizationHelper) {
		this.markdownGenerator = markdownGenerator;
		this.rateGeneralManager = rateGeneralManager;
		this.localizationHelper = localizationHelper;
	}

	@Scheduled(cron = "${cron.exchange.rates.update}")
	public void updateAllRates() {
		rateGeneralManager.commitAllRates();
	}

	public String markdownReport(String key) {
		ExchangeKey exchangeKey = ExchangeKey.checkExchangeKey(key);
		switch (exchangeKey) {
			default:
			case bank_ru: return markdownGenerator.bankRuReport();
			case bank_ua: return markdownGenerator.bankUaReport();
			case bank_by: return markdownGenerator.bankByReport();
			case bank_kz: return markdownGenerator.bankKzReport();
			case metal_ru: return markdownGenerator.metalRuReport();
		}
	}

	public String buttonLabel(String key) {
		ExchangeKey exchangeKey = ExchangeKey.checkExchangeKey(key);
		switch (exchangeKey) {
			default:
			case bank_ru: return localizationHelper.i18n("exchange.bank.ru.button");
			case bank_ua: return localizationHelper.i18n("exchange.bank.ua.button");
			case bank_by: return localizationHelper.i18n("exchange.bank.by.button");
			case bank_kz: return localizationHelper.i18n("exchange.bank.kz.button");
			case metal_ru: return localizationHelper.i18n("exchange.metal.ru.button");
		}
	}
}
