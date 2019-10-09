package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

public abstract class BotAdminCommand extends BotCommand {
	@Override
	public void prepare(DigestBot aDigestBot, Update aUpdate) {
		final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aUpdate.getMessage());
		if (lReceivedMessage.isIsUserAdmin()) {
			run(aDigestBot, lReceivedMessage);
		} else {
			// TODO: Set a normal error message.
			aDigestBot.sendSimpleMessage(lReceivedMessage.getChatId(),
			        lReceivedMessage.getMessageId(),"Error: You aren't admin!");
		}
	}

	public abstract void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage);
}
