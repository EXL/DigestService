package ru.exlmoto.digest.site.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.exchange.service.ExchangeService;
import ru.exlmoto.motofan.service.MotofanService;

import java.util.List;

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
		List<String> newMessages = motofanService.htmlMotofanPostsReport();
		StringBuilder stringBuilder = new StringBuilder();
		for (String message : newMessages) {
			stringBuilder.append(message).append("<br><br>");
		}
		return stringBuilder.toString();
	}
}
