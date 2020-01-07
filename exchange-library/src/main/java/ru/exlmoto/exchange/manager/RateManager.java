package ru.exlmoto.exchange.manager;

import lombok.extern.slf4j.Slf4j;

import ru.exlmoto.exchange.parser.RateParser;

@Slf4j
public abstract class RateManager {
	public void commitRates(String url) {
		commitRates(url, null);
	}

	protected void logRates(RateParser parser) {
		String message = "==> Using ";
		if (parser.isMirror()) {
			message += "mirror ";
		}
		message += parser.getClass().getName() + ".";
		log.info(message);
		parser.logParsedValues();
	}

	public abstract void commitRates(String url, String mirror);
}
