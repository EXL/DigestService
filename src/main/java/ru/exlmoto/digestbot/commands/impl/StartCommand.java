package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class StartCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(),
		        aReceivedMessage.getMessageId(),
		        lLocalizationHelper.getLocalizedString("digestbot.command.start"));
	}
}
