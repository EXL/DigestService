package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.services.impl.FileService;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class GameCommand extends BotCommand {
	private final String mGameImageUrl;

	private final FileService mFileService;

	@Autowired
	public GameCommand(final FileService aFileService,
	                   @Value("${digestbot.service.game}") final String aGameImageUrl) {
		mGameImageUrl = aGameImageUrl;
		mFileService = aFileService;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Pair<Boolean, String> lAnswer = mFileService.receiveObject(mGameImageUrl);
		final String lResult = lAnswer.getSecond();
		if (lAnswer.getFirst()) {
			aDigestBot.sendPhotoToChatFromUrl(aReceivedMessage.getChatId(),
				aReceivedMessage.getMessageId(),
				aReceivedMessage.getChatId(),
				aLocalizationHelper.getLocalizedString("digestbot.command.game"),
				lResult, true);
		} else {
			aDigestBot.getBotLogger().error(String.format("Cannot get game statistic: %s", lResult));
			aDigestBot.sendMarkdownMessage(aReceivedMessage.getChatId(),
				aReceivedMessage.getMessageId(),
				String.format(aLocalizationHelper.getLocalizedString("digestbot.error.game"), lResult));
		}
	}
}
