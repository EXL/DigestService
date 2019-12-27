package ru.exlmoto.exchange.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.exlmoto.exchange.domain.BankRuEntity;
import ru.exlmoto.exchange.repository.BankRuRepository;
import ru.exlmoto.exchange.repository.BankUaRepository;
import ru.exlmoto.exchange.repository.BankByRepository;
import ru.exlmoto.exchange.repository.BankKzRepository;
import ru.exlmoto.exchange.repository.MetalRuRepository;

@Component
@RequiredArgsConstructor
public class GeneratorMarkdown {
	private final GeneratorUtil util;

	private final BankRuRepository bankRuRepository;
	private final BankUaRepository bankUaRepository;
	private final BankByRepository bankByRepository;
	private final BankKzRepository bankKzRepository;
	private final MetalRuRepository metalRuRepository;

	public String bankRuReport() {
		BankRuEntity bankRuEntity = bankRuRepository.getBankRu();
		String report = util.i18n("bank.ru");
		if (true) {
			report += " " + util.i18n("change") + " " + bankRuEntity.getPrev();
		}
		report += "\n" + String.format(util.i18n("header"), bankRuEntity.getDate());
		report += "\n```\n";
		report += String.format("1 USD = %s RUB.\n", bankRuEntity.getUsd());
		report += String.format("1 EUR = %s RUB.\n", bankRuEntity.getEur());
		report += String.format("1 KZT = %s RUB.\n", bankRuEntity.getKzt());
		report += String.format("1 BYN = %s RUB.\n", bankRuEntity.getByn());
		report += String.format("1 UAH = %s RUB.\n", bankRuEntity.getUah());
		report += String.format("1 GBP = %s RUB.\n", bankRuEntity.getGbp());
		report += "```";
		return report;
	}
}
