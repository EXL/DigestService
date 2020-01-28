package ru.exlmoto.digest.bot.keyboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.keyboard.impl.ChartKeyboard;
import ru.exlmoto.digest.bot.keyboard.impl.RateKeyboard;
import ru.exlmoto.digest.bot.keyboard.impl.SubscribeKeyboard;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BotKeyboardFactory {
	private final Logger log = LoggerFactory.getLogger(BotKeyboardFactory.class);

	private final ApplicationContext context;

	private final Map<String, BotKeyboard> keyboardMap = new HashMap<>();

	public BotKeyboardFactory(ApplicationContext context) {
		this.context = context;
	}

	@PostConstruct
	private void registerKeyboards() {
		addKeyboard(BotKeyboard.CHART, ChartKeyboard.class);
		addKeyboard(BotKeyboard.RATE, RateKeyboard.class);
		addKeyboard(BotKeyboard.SUBSCRIBE, SubscribeKeyboard.class);
	}

	private <T extends BotKeyboard> void addKeyboard(String keyboard, Class<T> botKeyboardClass) {
		keyboardMap.put(keyboard, context.getBean(botKeyboardClass));
		log.info(String.format("Registered bot Keyboard: '%s' on '%s' class.",
			keyboard.replaceAll(BotKeyboard.DELIMITER, ""), botKeyboardClass.getSimpleName()));
	}

	public Optional<BotKeyboard> getKeyboard(String keyboard) {
		return Optional.ofNullable(keyboardMap.get(keyboard));
	}
}
