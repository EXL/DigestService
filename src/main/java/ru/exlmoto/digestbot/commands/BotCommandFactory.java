package ru.exlmoto.digestbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.commands.impl.HelloCommand;
import ru.exlmoto.digestbot.commands.impl.SendCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BotCommandFactory {
	private Map<String, BotCommand> mBotCommandsMap;

	@Autowired
	public BotCommandFactory(ApplicationContext aApplicationContext) {
		mBotCommandsMap = new HashMap<>();
		mBotCommandsMap.put("/hello", aApplicationContext.getBean(HelloCommand.class));
		mBotCommandsMap.put("/hi", aApplicationContext.getBean(HelloCommand.class));
		mBotCommandsMap.put("/send", aApplicationContext.getBean(SendCommand.class));
	}

	public Optional<BotCommand> getCommand(String aCommandName) {
		return Optional.ofNullable(mBotCommandsMap.get(aCommandName));
	}
}
