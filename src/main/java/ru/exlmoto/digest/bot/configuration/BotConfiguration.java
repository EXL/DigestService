package ru.exlmoto.digest.bot.configuration;

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
	private int maxUpdates;
	private int maxSendLength;
	private int callbackCooldown;
	private boolean enableNotifications;
	private boolean debugLogUpdates;
	private boolean debugShowGreetings;
	private boolean debugSilent;
	private boolean debugLogSends;
	private boolean debugUseStack;

	@PostConstruct
	private void telegramBotApiInitialization() {
		ApiContextInitializer.init();
	}
}
