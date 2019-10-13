package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.YamlLocalizationHelper;

@Component
public class ChartsCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendMarkdownMessageWithKeyboard(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
			aLocalizationHelper.getLocalizedString("command.charts"), null);
	}
}
