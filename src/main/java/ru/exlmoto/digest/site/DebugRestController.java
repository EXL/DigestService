package ru.exlmoto.digest.site;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.exlmoto.digest.chart.ChartService;
import ru.exlmoto.digest.exchange.ExchangeService;
import ru.exlmoto.digest.exchange.util.ExchangeKeys;
import ru.exlmoto.digest.motofan.MotofanService;

import java.util.List;

@RestController
public class DebugRestController {
	private final ExchangeService exchangeService;
	private final MotofanService motofanService;
	private final ChartService chartService;

	public DebugRestController(ExchangeService exchangeService,
	                           MotofanService motofanService,
	                           ChartService chartService) {
		this.exchangeService = exchangeService;
		this.motofanService = motofanService;
		this.chartService = chartService;
	}

	@GetMapping("/")
	public String root() {
		return "Hello World!";
	}

	@GetMapping("/ex")
	public String home() {
		return
			exchangeService.markdownReport(ExchangeKeys.BANK_RU) + "\n\n" +
			exchangeService.markdownReport(ExchangeKeys.BANK_UA) + "\n\n" +
			exchangeService.markdownReport(ExchangeKeys.BANK_BY) + "\n\n" +
			exchangeService.markdownReport(ExchangeKeys.BANK_KZ) + "\n\n" +
			exchangeService.markdownReport(ExchangeKeys.METAL_RU);
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

	@GetMapping("/chart")
	public String chart() {
		return
			chartService.getChartKeys().toString() + "<br><br>" +
			chartService.getButtonLabel("usd_rub") + "<br><br>" +
			chartService.getChart("usd_rub");
	}
}
