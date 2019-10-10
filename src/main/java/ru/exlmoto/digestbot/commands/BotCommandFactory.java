package ru.exlmoto.digestbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.commands.impl.*;

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
	public BotCommandFactory(final ApplicationContext aApplicationContext,
	                         @Value("${digestbot.name}") final String aBotUsername,
	                         @Value("${general.username.cast}") final String aUsernameCast) {
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
		addCommand("/start", mApplicationContext.getBean(StartCommand.class));
		addCommand("/help", mApplicationContext.getBean(HelpCommand.class));
		addCommand("/hostip", mApplicationContext.getBean(HostIpCommand.class));
		addCommand("/game", mApplicationContext.getBean(GameCommand.class));
	}

	/**
	 * The second addition of the same command to the hash table is respond to the command with the bot nickname.
	 * Example: "/hello" and "/hello@Digest_bot" commands.
	 */
	private void addCommand(final String aCommand, final BotCommand aBotCommandClass) {
		mBotCommandsMap.put(aCommand, aBotCommandClass);
		mBotCommandsMap.put(aCommand + mUsernameCast + mBotUsername, aBotCommandClass);
	}

	public Optional<BotCommand> getCommand(String aCommandName) {
		return Optional.ofNullable(mBotCommandsMap.get(aCommandName));
	}
}
