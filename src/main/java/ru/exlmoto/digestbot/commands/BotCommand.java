package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

public abstract class BotCommand {
	public void prepare(final DigestBot aDigestBot, final Message aMessage) {
		new Thread(() -> run(aDigestBot, aDigestBot.getLocalizationHelper(),
			aDigestBot.createReceivedMessage(aMessage))).start();
	}

	public abstract void run(final DigestBot aDigestBot,
	                         final LocalizationHelper aLocalizationHelper,
	                         final ReceivedMessage aReceivedMessage);
}
