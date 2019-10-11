package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class HelloCommand extends BotAdminCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			aLocalizationHelper.getRandomLocalizedStringWithUsername("digestbot.command.hello",
				aReceivedMessage.getMessageUsername()));
	}
}
