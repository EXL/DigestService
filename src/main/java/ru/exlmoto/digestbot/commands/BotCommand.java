package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

public abstract class BotCommand {
	public void prepare(final DigestBot aDigestBot, final Message aMessage) {
		run(aDigestBot, aDigestBot.createReceivedMessage(aMessage));
	}

	public abstract void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage);
}
