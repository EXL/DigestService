package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class ChartsCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendMarkdownMessageWithKeyboard(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
			aLocalizationHelper.getLocalizedString("digestbot.command.charts"), null);
	}
}
