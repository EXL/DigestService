package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.annotations.ChatCommand;
import ru.exlmoto.digestbot.commands.Command;
import ru.exlmoto.digestbot.commands.CommandsContext;

@Component
@ChatCommand(name = "/hello")
public class HelloCommand implements Command {
	private DigestBot mDigestBot;

	public HelloCommand(DigestBot aDigestBot) {
		this.mDigestBot = aDigestBot;
	}

	@Override
	public void execute(CommandsContext aCommandsContext) {
		this.mDigestBot.sendSimpleMessage(aCommandsContext.getChatId(), aCommandsContext.getText());
	}
}
