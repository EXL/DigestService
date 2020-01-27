package ru.exlmoto.digest.bot.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.impl.CoffeeCommand;
import ru.exlmoto.digest.bot.ability.impl.GameCommand;
import ru.exlmoto.digest.bot.ability.impl.HelloCommand;
import ru.exlmoto.digest.bot.ability.impl.HelpCommand;
import ru.exlmoto.digest.bot.ability.impl.HostIpCommand;
import ru.exlmoto.digest.bot.ability.impl.SendCommand;
import ru.exlmoto.digest.bot.ability.impl.StartCommand;
import ru.exlmoto.digest.bot.ability.impl.ChartCommand;
import ru.exlmoto.digest.bot.telegram.BotTelegram;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BotAbilityFactory {
	private final Logger log = LoggerFactory.getLogger(BotAbilityFactory.class);

	private final ApplicationContext context;
	private final BotTelegram telegram;

	private final Map<String, BotAbility> abilityMap = new HashMap<>();

	public BotAbilityFactory(ApplicationContext context, BotTelegram telegram) {
		this.context = context;
		this.telegram = telegram;
	}

	@PostConstruct
	private void registerAbilities() {
		addCommand("/hello", HelloCommand.class);
		addCommand("/hi", HelloCommand.class);
		addCommand("/start", StartCommand.class);
		addCommand("/help", HelpCommand.class);
		addCommand("/coffee", CoffeeCommand.class);
		addCommand("/hostip", HostIpCommand.class);
		addCommand("/game", GameCommand.class);
		addCommand("/send", SendCommand.class);
		addCommand("/sticker", SendCommand.class);
		addCommand("/image", SendCommand.class);
		addCommand("/charts", ChartCommand.class);
	}

	/*
	 * The second addition of the same command to the hash map is respond to the command with the bot nickname.
	 * Example: "/hello" and "/hello@Digest_bot" commands.
	 */
	private <T extends BotAbility> void addCommand(String command, Class<T> botAbilityClass) {
		String fullCommand = command + "@" + telegram.getUsername();
		abilityMap.put(command, context.getBean(botAbilityClass));
		abilityMap.put(fullCommand, context.getBean(botAbilityClass));
		log.info(String.format("Registered bot Command: '%s' alias '%s' on '%s' class.",
			command, fullCommand, botAbilityClass.getSimpleName()));
	}

	private <T extends BotAbility> void addHashTag(String hashTag, Class<T> botAbilityClass) {
		abilityMap.put(hashTag, context.getBean(botAbilityClass));
		log.info(String.format("Registered bot HashTag: '%s' on '%s' class.",
			hashTag, botAbilityClass.getSimpleName()));
	}

	public Optional<BotAbility> getAbility(String ability) {
		return Optional.ofNullable(abilityMap.get(ability));
	}
}
