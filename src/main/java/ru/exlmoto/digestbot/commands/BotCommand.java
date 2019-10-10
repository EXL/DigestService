package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

public abstract class BotCommand {
	public void prepare(final DigestBot aDigestBot, final Update aUpdate) {
		final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aUpdate.getMessage());
		run(aDigestBot, lReceivedMessage);
	}

	public abstract void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage);
}
