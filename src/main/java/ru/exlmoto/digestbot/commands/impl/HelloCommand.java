package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

@Component
public class HelloCommand extends BotCommand {
	@Override
	public void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage) {
		// TODO: Set random message as answer.
		aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(), aReceivedMessage.toString());
	}
}
