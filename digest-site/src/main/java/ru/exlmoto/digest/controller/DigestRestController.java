package ru.exlmoto.digest.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.exchange.service.ExchangeService;
import ru.exlmoto.motofan.service.MotofanService;

@RequiredArgsConstructor
@RestController
public class DigestRestController {
	private final ExchangeService exchangeService;
	private final MotofanService motofanService;

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

	@GetMapping("/motofan")
	public String motofan() {
		return motofanService.htmlMotofanPostsReport().toString();
	}
}
