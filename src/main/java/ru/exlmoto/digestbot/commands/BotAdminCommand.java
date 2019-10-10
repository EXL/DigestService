package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

public abstract class BotAdminCommand extends BotCommand {
	@Override
	public void prepare(final DigestBot aDigestBot, final Update aUpdate) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aUpdate.getMessage());
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
