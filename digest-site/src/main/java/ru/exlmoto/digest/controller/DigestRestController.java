package ru.exlmoto.digest.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.exchange.service.ExchangeService;

@RequiredArgsConstructor
@RestController
public class DigestRestController {
	private final ExchangeService exchangeService;

	@GetMapping("/")
	public String home() {
		return
			exchangeService.markdownBankRuReport() + "\n\n" +
				exchangeService.markdownBankUaReport() + "\n\n" +
				exchangeService.markdownBankByReport() + "\n\n" +
				exchangeService.markdownBankKzReport() + "\n\n" +
				exchangeService.markdownMetalRuReport();
	}

	@GetMapping("/refresh")
	public String refresh() {
		exchangeService.updateAllRates();
		return home();
	}
}
