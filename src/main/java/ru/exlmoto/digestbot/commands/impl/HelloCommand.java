package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;

@Component
public class HelloCommand implements BotCommand {
	@Override
	public void run(DigestBot aDigestBot, Update aUpdate) {
		final Long lChatId = aUpdate.getMessage().getChatId();
		
		aDigestBot.sendSimpleMessage(lChatId, "test");
	}
}
