package ru.exlmoto.digest.site;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.motofan.MotofanService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DebugRestController {
	private final ExchangeService exchangeService;
	private final MotofanService motofanService;

	@GetMapping("/")
	public String root() {
		return "Hello World!";
	}

	@GetMapping("/ex")
	public String home() {
		return
			exchangeService.markdownBankRuReport() + "\n\n" +
			exchangeService.markdownBankUaReport() + "\n\n" +
			exchangeService.markdownBankByReport() + "\n\n" +
			exchangeService.markdownBankKzReport() + "\n\n" +
			exchangeService.markdownMetalRuReport();
	}

	@GetMapping("/ex/refresh")
	public String refresh() {
		exchangeService.updateAllRates();
		return "Updated!";
	}

	@GetMapping("/motofan")
	public String motofan() {
		List<String> newMessages = motofanService.getLastMotofanPostsInHtml();
		StringBuilder stringBuilder = new StringBuilder();
		for (String message : newMessages) {
			stringBuilder.append(message).append("<br><br>");
		}
		return stringBuilder.toString();
	}
}