package ru.exlmoto.engine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.exchange.service.ExchangeService;

@RestController
public class EngineRestController {
	private final ExchangeService exchangeService;

	public EngineRestController(ExchangeService exchangeService) {
		this.exchangeService = exchangeService;
	}

	@GetMapping("/")
	public String home() {
		exchangeService.updateAllRates();
		return "Hello World";
	}
}
