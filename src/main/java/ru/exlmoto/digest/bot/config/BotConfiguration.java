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

	@PostConstruct
	private void telegramBotApiInitialization() {
		ApiContextInitializer.init();
	}
}
