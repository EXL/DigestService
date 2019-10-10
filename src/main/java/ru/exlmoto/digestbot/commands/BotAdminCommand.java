package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

public abstract class BotAdminCommand extends BotCommand {
	@Override
	public void prepare(final DigestBot aDigestBot, final Update aUpdate, final boolean aIsEdited) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		final Message lMessage = (aIsEdited) ? aUpdate.getEditedMessage() : aUpdate.getMessage();
		final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(lMessage);
		if (lReceivedMessage.isIsUserAdmin()) {
			run(aDigestBot, lReceivedMessage);
		} else {
			aDigestBot.sendSimpleMessage(lReceivedMessage.getChatId(),
			        lReceivedMessage.getMessageId(),
			        lLocalizationHelper.getLocalizedStringWithUsername("digestbot.error.access",
			                lReceivedMessage.getMessageUsername()));
		}
	}

	public abstract void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage);
}
