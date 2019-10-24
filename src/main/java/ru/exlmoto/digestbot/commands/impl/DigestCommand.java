package ru.exlmoto.digestbot.commands.impl;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DigestCommand extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Long lChatId = aReceivedMessage.getChatId();
		final Integer lMessageId = aReceivedMessage.getMessageId();
		final YamlLocalizationHelper lYamlLocalizationHelper = aDigestBot.getLocalizationHelper();
		final Pair<Boolean, String> lUsername = aReceivedMessage.getAvailableUsername();

		final String lHello = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.hello", lUsername);
		final String lEmpty = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.empty", lUsername);
		final String lHeader = lYamlLocalizationHelper.getRandomLocalizedString("command.digest.header");

		String lAnswer = "";

		if (true) {
			lAnswer += lHello + '\n' + lHeader + " 1:\n";
		} else {
			lAnswer += lEmpty;
		}

		aDigestBot.sendMarkdownMessageWithKeyboard(lChatId, lMessageId, lAnswer,
			aDigestBot.getDigestKeyboard().getDigestKeyboard());
	}
}
