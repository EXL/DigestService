package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.YamlLocalizationHelper;

@Component
public class HelloCommand extends BotAdminCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			aLocalizationHelper.getRandomLocalizedString("command.hello",
				aReceivedMessage.getMessageUsername()));
	}
}
