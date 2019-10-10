package ru.exlmoto.digestbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.commands.impl.CoffeeCommand;
import ru.exlmoto.digestbot.commands.impl.HelloCommand;
import ru.exlmoto.digestbot.commands.impl.SendCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BotCommandFactory {
	private final ApplicationContext mApplicationContext;

	private final String mBotUsername;
	private final String mUsernameCast;

	private final Map<String, BotCommand> mBotCommandsMap;

	@Autowired
	public BotCommandFactory(ApplicationContext aApplicationContext,
	                         @Value("${digestbot.name}") String aBotUsername,
	                         @Value("${general.username.cast}") String aUsernameCast) {
		mApplicationContext = aApplicationContext;
		mBotUsername = aBotUsername;
		mUsernameCast = aUsernameCast;

		mBotCommandsMap = new HashMap<>();
		registerCommands();
	}

	private void registerCommands() {
		addCommand("/hello", mApplicationContext.getBean(HelloCommand.class));
		addCommand("/hi", mApplicationContext.getBean(HelloCommand.class));
		addCommand("/send", mApplicationContext.getBean(SendCommand.class));
		addCommand("/sticker", mApplicationContext.getBean(SendCommand.class));
		addCommand("/coffee", mApplicationContext.getBean(CoffeeCommand.class));
	}

	public Optional<BotCommand> getCommand(String aCommandName) {
		return Optional.ofNullable(mBotCommandsMap.get(aCommandName));
	}

	/**
	 * The second addition of the command to the table to respond
	 * to the command with the nickname of the bot, i.e "/hello" and "/hello@Digest_bot".
	 */
	private void addCommand(final String aCommand, final BotCommand aBotCommandClass) {
		mBotCommandsMap.put(aCommand, aBotCommandClass);
		mBotCommandsMap.put(aCommand + mUsernameCast + mBotUsername, aBotCommandClass);
	}
}
