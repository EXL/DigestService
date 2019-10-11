package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

public abstract class BotAdminCommand extends BotCommand {
	@Override
	public void prepare(final DigestBot aDigestBot, final Message aMessage) {
		new Thread(() -> {
			final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
			final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aMessage);
			if (lReceivedMessage.isIsUserAdmin()) {
				run(aDigestBot, lLocalizationHelper, lReceivedMessage);
			} else {
				aDigestBot.sendSimpleMessage(lReceivedMessage.getChatId(),
					lReceivedMessage.getMessageId(),
					lLocalizationHelper.getLocalizedStringWithUsername("digestbot.error.access",
						lReceivedMessage.getMessageUsername()));
			}
		}).start();
	}

	@Override
	public abstract void run(final DigestBot aDigestBot,
	                         final LocalizationHelper localizationHelper,
	                         final ReceivedMessage aReceivedMessage);
}
