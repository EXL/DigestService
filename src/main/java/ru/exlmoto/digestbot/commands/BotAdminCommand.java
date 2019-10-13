package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.YamlLocalizationHelper;

public abstract class BotAdminCommand extends BotCommand {
	@Override
	public void prepare(final DigestBot aDigestBot, final Message aMessage) {
		new Thread(() -> {
			final YamlLocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
			final ReceivedMessage lReceivedMessage = aDigestBot.createReceivedMessage(aMessage);
			if (lReceivedMessage.isIsUserAdmin()) {
				run(aDigestBot, lLocalizationHelper, lReceivedMessage);
			} else {
				aDigestBot.sendSimpleMessage(lReceivedMessage.getChatId(),
					lReceivedMessage.getMessageId(),
					lLocalizationHelper.getLocalizedString("error.access",
						lReceivedMessage.getMessageUsername()));
			}
		}).start();
	}

	@Override
	public abstract void run(final DigestBot aDigestBot,
	                         final YamlLocalizationHelper localizationHelper,
	                         final ReceivedMessage aReceivedMessage);
}
