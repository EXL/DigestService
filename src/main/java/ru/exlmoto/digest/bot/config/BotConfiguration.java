package ru.exlmoto.digest.bot.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {
	String token;
	String username;
	boolean logUpdates;
}
