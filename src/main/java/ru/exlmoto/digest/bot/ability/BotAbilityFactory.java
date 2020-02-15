package ru.exlmoto.digest.bot.ability;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.ability.keyboard.impl.ChartKeyboard;
import ru.exlmoto.digest.bot.ability.keyboard.impl.DigestKeyboard;
import ru.exlmoto.digest.bot.ability.keyboard.impl.RateKeyboard;
import ru.exlmoto.digest.bot.ability.keyboard.impl.ShowKeyboard;
import ru.exlmoto.digest.bot.ability.keyboard.impl.SubscribeKeyboard;
import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.ability.message.impl.CoffeeCommand;
import ru.exlmoto.digest.bot.ability.message.impl.ChartCommand;
import ru.exlmoto.digest.bot.ability.message.impl.DebugCommand;
import ru.exlmoto.digest.bot.ability.message.impl.DeleteCommand;
import ru.exlmoto.digest.bot.ability.message.impl.DigestCommand;
import ru.exlmoto.digest.bot.ability.message.impl.DigestHashTag;
import ru.exlmoto.digest.bot.ability.message.impl.GameCommand;
import ru.exlmoto.digest.bot.ability.message.impl.HelloCommand;
import ru.exlmoto.digest.bot.ability.message.impl.HelpCommand;
import ru.exlmoto.digest.bot.ability.message.impl.HostIpCommand;
import ru.exlmoto.digest.bot.ability.message.impl.RateCommand;
import ru.exlmoto.digest.bot.ability.message.impl.SendCommand;
import ru.exlmoto.digest.bot.ability.message.impl.ShowCommand;
import ru.exlmoto.digest.bot.ability.message.impl.StartCommand;
import ru.exlmoto.digest.bot.ability.message.impl.SubscribeCommand;
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

	private final Map<String, BotAbility<Message>> messageMap = new HashMap<>();
	private final Map<String, BotAbility<CallbackQuery>> keyboardMap = new HashMap<>();

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
		addCommand("/chart", ChartCommand.class);
		addCommand("/rate", RateCommand.class);
		addCommand("/subscribe", SubscribeCommand.class);
		addCommand("/show", ShowCommand.class);
		addCommand("/delete", DeleteCommand.class);
		addCommand("/digest", DigestCommand.class);
		addCommand("/debug", DebugCommand.class);

		addHashTag("#digest", DigestHashTag.class);
		addHashTag("#news", DigestHashTag.class);

		addKeyboard(Keyboard.chart, ChartKeyboard.class);
		addKeyboard(Keyboard.rate, RateKeyboard.class);
		addKeyboard(Keyboard.subscribe, SubscribeKeyboard.class);
		addKeyboard(Keyboard.digest, DigestKeyboard.class);
		addKeyboard(Keyboard.show, ShowKeyboard.class);
	}

	/*
	 * The second addition of the same command to the hash map is respond to the command with the bot nickname.
	 * Example: "/hello" and "/hello@Digest_bot" commands.
	 */
	private <T extends MessageAbility> void addCommand(String command, Class<T> botAbilityClass) {
		String fullCommand = command + telegram.getUsername();
		messageMap.put(command, context.getBean(botAbilityClass));
		messageMap.put(fullCommand, context.getBean(botAbilityClass));
		log.info(String.format("Registered bot Command: '%s' alias '%s' on '%s' class.",
			command, fullCommand, botAbilityClass.getSimpleName()));
	}

	private <T extends MessageAbility> void addHashTag(String hashTag, Class<T> botAbilityClass) {
		messageMap.put(hashTag, context.getBean(botAbilityClass));
		log.info(String.format("Registered bot HashTag: '%s' on '%s' class.",
			hashTag, botAbilityClass.getSimpleName()));
	}

	private <T extends KeyboardAbility> void addKeyboard(Keyboard keyboard, Class<T> botKeyboardClass) {
		keyboardMap.put(keyboard.withName(), context.getBean(botKeyboardClass));
		log.info(String.format("Registered bot Keyboard: '%s' on '%s' class.",
			keyboard.name(), botKeyboardClass.getSimpleName()));
	}

	public Optional<BotAbility<Message>> getMessageAbility(String ability) {
		return Optional.ofNullable(messageMap.get(ability));
	}

	public Optional<BotAbility<CallbackQuery>> getKeyboardAbility(String ability) {
		return Optional.ofNullable(keyboardMap.get(Keyboard.chopKeyboardNameRight(ability)));
	}
}
