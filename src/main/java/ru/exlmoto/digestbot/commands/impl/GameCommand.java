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
	public void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		new Thread(() -> aDigestBot.sendPhotoToChatFromUrl(aReceivedMessage.getChatId(),
		        aReceivedMessage.getMessageId(), aReceivedMessage.getChatId(),
		        lLocalizationHelper.getLocalizedString("digestbot.command.game"), mGameImageUrl)).start();
	}
}
