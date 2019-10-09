package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

public abstract class BotCommand {
	public abstract void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage);

	public void prepare(DigestBot aDigestBot, Update aUpdate) {
		ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aUpdate.getMessage());
		run(aDigestBot, lReceivedMessage);
	}
}
