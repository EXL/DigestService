package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.RandomCatchPhrases;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class HelloCommand extends BotAdminCommand {
	private final RandomCatchPhrases mRandomCatchPhrases;

	@Autowired
	public HelloCommand(RandomCatchPhrases aRandomCatchPhrases) {
		mRandomCatchPhrases = aRandomCatchPhrases;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			mRandomCatchPhrases.getRandomLocalizedStringWithUsername("digestbot.command.hello",
				aReceivedMessage.getMessageUsername()));
	}
}
