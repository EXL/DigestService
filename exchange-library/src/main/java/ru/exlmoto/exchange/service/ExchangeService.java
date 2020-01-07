package ru.exlmoto.exchange.service;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.exlmoto.exchange.generator.MarkdownGenerator;
import ru.exlmoto.exchange.manager.RateGeneralManager;

@RequiredArgsConstructor
@Service
public class ExchangeService {
	private final MarkdownGenerator markdownGenerator;
	private final RateGeneralManager rateGeneralManager;

	@Scheduled(cron = "${cron.exchange.update}")
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
