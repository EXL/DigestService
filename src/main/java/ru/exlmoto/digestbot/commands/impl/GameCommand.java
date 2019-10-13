package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.services.impl.FileService;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.impl.YamlLocalizationHelper;

@Component
public class GameCommand extends BotCommand {
	private final String mGameImageUrl;
	private final Boolean mFileDownloader;

	private final FileService mFileService;

	@Autowired
	public GameCommand(final FileService aFileService,
	                   @Value("${digestbot.service.game}") final String aGameImageUrl,
	                   @Value("${digestbot.file.downloader}") final Boolean aFileDownloader) {
		mFileService = aFileService;
		mGameImageUrl = aGameImageUrl;
		mFileDownloader = aFileDownloader;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		final Long lChatId = aReceivedMessage.getChatId();
		final Integer lMessageId = aReceivedMessage.getMessageId();
		final String lCaption = aLocalizationHelper.getLocalizedString("command.game");
		if (mFileDownloader) {
			final Pair<Boolean, String> lAnswer = mFileService.receiveObject(mGameImageUrl);
			final String lResult = lAnswer.getSecond();
			if (lAnswer.getFirst()) {
				aDigestBot.sendPhotoToChatFromUrl(lChatId, lMessageId, lChatId, lCaption, lResult, true);
			} else {
				aDigestBot.getBotLogger().error(String.format("Cannot get game statistic: '%s'.", lResult));
				aDigestBot.sendMarkdownMessage(lChatId, lMessageId,
					String.format(aLocalizationHelper.getLocalizedString("error.game"), lResult));
			}
		} else {
			aDigestBot.sendPhotoToChatFromUrl(lChatId, lMessageId, lChatId, lCaption, mGameImageUrl, false);
		}
	}
}
