package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class DigestHashTag extends BotCommand {
	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		String lMessageText = aReceivedMessage.getMessageText();

		// Remove hash tags
		lMessageText = lMessageText.replaceAll("#digest|#news", "").trim();

		if (!lMessageText.isEmpty()) {
			aDigestBot.sendSimpleMessage(aReceivedMessage.getChatId(), aReceivedMessage.getMessageId(),
					aLocalizationHelper.getRandomLocalizedString("hashtag.digest.ok",
							aReceivedMessage.getAvailableUsername()));
		}
	}
}
