package ru.exlmoto.digest.exchange.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;

import ru.exlmoto.digest.exchange.parser.RateParser;

public abstract class RateManager {
	private final Logger log = LoggerFactory.getLogger(RateManager.class);

	public void commitRates(String url) {
		try {
			commitRates(url, null);
		} catch (DataAccessException dae) {
			log.error("Cannot save object to database.", dae);
		}
	}

	protected void logRates(RateParser parser) {
		String message = "==> Using ";
		if (parser.isMirror()) {
			message += "mirror ";
		}
		message += parser.getClass().getSimpleName() + ".";
		log.info(message);
		parser.logParsedValues();
	}

	public abstract void commitRates(String url, String mirror);
}
