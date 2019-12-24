package ru.exlmoto.exchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service")
public class ExchangeConfig {
	/**
	 * A message for the service.
	 */
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
