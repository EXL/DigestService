package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.TgMarkdownGenerator;
import ru.exlmoto.digest.exchange.manager.RateGeneralManager;
import ru.exlmoto.digest.exchange.util.ExchangeKeys;
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
		switch (key) {
			default:
			case ExchangeKeys.BANK_RU: return markdownGenerator.bankRuReport();
			case ExchangeKeys.BANK_UA: return markdownGenerator.bankUaReport();
			case ExchangeKeys.BANK_BY: return markdownGenerator.bankByReport();
			case ExchangeKeys.BANK_KZ: return markdownGenerator.bankKzReport();
			case ExchangeKeys.METAL_RU: return markdownGenerator.metalRuReport();
		}
	}

	public String buttonLabel(String key) {
		switch (key) {
			default:
			case ExchangeKeys.BANK_RU: return localizationHelper.i18n("exchange.bank.ru.button");
			case ExchangeKeys.BANK_UA: return localizationHelper.i18n("exchange.bank.ua.button");
			case ExchangeKeys.BANK_BY: return localizationHelper.i18n("exchange.bank.by.button");
			case ExchangeKeys.BANK_KZ: return localizationHelper.i18n("exchange.bank.kz.button");
			case ExchangeKeys.METAL_RU: return localizationHelper.i18n("exchange.metal.ru.button");
		}
	}
}
