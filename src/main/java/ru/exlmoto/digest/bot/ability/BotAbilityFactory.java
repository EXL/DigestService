package ru.exlmoto.digest.bot.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.impl.HelloCommand;
import ru.exlmoto.digest.bot.ability.impl.HelpCommand;
import ru.exlmoto.digest.bot.ability.impl.StartCommand;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BotAbilityFactory {
	private final Logger log = LoggerFactory.getLogger(BotAbilityFactory.class);

	private final ApplicationContext context;
	private final BotConfiguration config;

	private final Map<String, BotAbility> abilityMap = new HashMap<>();

	public BotAbilityFactory(ApplicationContext context, BotConfiguration config) {
		this.context = context;
		this.config = config;
	}

	@PostConstruct
	private void setUp() {
		registerAbilities();
	}

	private void registerAbilities() {
		addAbility("/hello", HelloCommand.class);
		addAbility("/hi", HelloCommand.class);
		addAbility("/start", StartCommand.class);
		addAbility("/help", HelpCommand.class);
	}

	/*
	 * The second addition of the same command to the hash map is respond to the command with the bot nickname.
	 * Example: "/hello" and "/hello@Digest_bot" commands.
	 */
	private <T extends BotAbility> void addAbility(String ability, Class<T> botAbilityClass) {
		log.info(String.format("Registering bot ability: '%s'.", ability));
		abilityMap.put(ability, context.getBean(botAbilityClass));
		abilityMap.put(ability + "@" + config.getUsername(), context.getBean(botAbilityClass));
	}

	public Optional<BotAbility> getAbility(String ability) {
		return Optional.ofNullable(abilityMap.get(ability));
	}
}
