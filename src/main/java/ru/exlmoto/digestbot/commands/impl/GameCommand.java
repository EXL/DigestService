package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class GameCommand extends BotCommand {
	private final String mGameImageUrl;

	public GameCommand(@Value("${digestbot.service.game}") final String aGameImageUrl) {
		mGameImageUrl = aGameImageUrl;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendPhotoToChatFromUrl(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			aReceivedMessage.getChatId(),
			aLocalizationHelper.getLocalizedString("digestbot.command.game"),
			mGameImageUrl);
	}
}
