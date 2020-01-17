package ru.exlmoto.digest.bot.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.telegram.telegrambots.ApiContextInitializer;

import javax.annotation.PostConstruct;

@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	private String token;
	private String username;
	private boolean logUpdates;
	private boolean showGreetings;
	private int maxUpdates;

	@PostConstruct
	private void telegramBotApiInitialization() {
		ApiContextInitializer.init();
	}
}
