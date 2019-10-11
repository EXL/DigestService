package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class HelpCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		String lHelpText = aLocalizationHelper.getLocalizedString("digestbot.command.help");
		if (aReceivedMessage.isIsUserAdmin()) {
			lHelpText += '\n' + aLocalizationHelper.getLocalizedString("digestbot.command.help.admin");
		}
		aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			lHelpText);
	}
}
