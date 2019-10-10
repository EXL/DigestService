package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class ImageCommand extends BotAdminCommand {
	@Override
	public void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		String lCommandText = aReceivedMessage.getMessageText();
		Long lChatId = aReceivedMessage.getChatId();
		final String[] lCommandWithArgs = lCommandText.split(" ");
		boolean lIsSameChat = false;
		boolean lError = false;

		if (lCommandWithArgs.length == 3) {
			// Determine chat id.
			final String lStringChatId = lCommandWithArgs[1];
			try {
				lChatId = NumberUtils.parseNumber(lStringChatId, Long.class);
			} catch (NumberFormatException e) {
				lError = true;
				lCommandText = lLocalizationHelper.getLocalizedString("digestbot.error.chatid");
			}
			if (!lError) {
				// Delete command and chat id from text.
				lCommandText = lCommandText.replaceFirst(lCommandWithArgs[0], "")
				                           .replaceFirst(lCommandWithArgs[1], "");
			}
		} else {
			lError = true;
			lCommandText = lLocalizationHelper.getLocalizedString("digestbot.error.image.format");
		}

		lIsSameChat = aReceivedMessage.getChatId().equals(lChatId);
		if (!lError) {
			aDigestBot.sendPhotoToChatFromUrl(lChatId,
			        (lIsSameChat) ? aReceivedMessage.getMessageId() : null,
			        aReceivedMessage.getChatId(), null, lCommandText.trim());
		} else {
			aDigestBot.sendMarkdownMessage(lChatId,
			        (lIsSameChat) ? aReceivedMessage.getMessageId() : null, lCommandText);
		}
	}
}
