package ru.exlmoto.digest.exchange;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.digest.exchange.generator.TgMarkdownGenerator;
import ru.exlmoto.digest.exchange.manager.RateGeneralManager;

@Service
public class ExchangeService {
	private final TgMarkdownGenerator markdownGenerator;
	private final RateGeneralManager rateGeneralManager;

	public ExchangeService(TgMarkdownGenerator markdownGenerator, RateGeneralManager rateGeneralManager) {
		this.markdownGenerator = markdownGenerator;
		this.rateGeneralManager = rateGeneralManager;
	}

	@Scheduled(cron = "${cron.exchange.rates.update}")
	public void updateAllRates() {
		rateGeneralManager.commitAllRates();
	}

	public String markdownBankRuReport() {
		return markdownGenerator.bankRuReport();
	}

	public String markdownBankUaReport() {
		return markdownGenerator.bankUaReport();
	}

	public String markdownBankByReport() {
		return markdownGenerator.bankByReport();
	}

	public String markdownBankKzReport() {
		return markdownGenerator.bankKzReport();
	}

	public String markdownMetalRuReport() {
		return markdownGenerator.metalRuReport();
	}
}
