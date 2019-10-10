package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class HelloCommand extends BotAdminCommand {
	@Override
	public void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		aDigestBot.sendSimpleMessage(
		        aReceivedMessage.getChatId(),
		        aReceivedMessage.getMessageId(),
		        lLocalizationHelper.getRandomLocalizedStringWithUsername("digestbot.command.hello",
		            aReceivedMessage.getMessageUsername()));
	}
}
