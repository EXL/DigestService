package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.utils.LocalizationHelper;

@Component
public class SendCommand extends BotAdminCommand {
	private final String SEND_COMMAND = "/send";
	private final String STICKER_COMMAND = "/sticker";

	/**
	 * Send command looks like: "/send <chat id> <message>".
	 * Sticker command looks like: "/sticker <chat id> <sticker id>".
	 */
	@Override
	public void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		String lCommandText = aReceivedMessage.getMessageText();
		Long lChatId = aReceivedMessage.getChatId();
		final String[] lCommandWithArgs = lCommandText.split(" ");
		boolean lIsSameChat = false;
		boolean lIsStickerMode = false;
		boolean lError = false;
		String lCommand = SEND_COMMAND;
		if (lCommandWithArgs[0].trim().startsWith(STICKER_COMMAND)) {
			lCommand = STICKER_COMMAND;
			lIsStickerMode = true;
		}

		if ((!lIsStickerMode && lCommandWithArgs.length >= 3) || (lIsStickerMode && lCommandWithArgs.length == 3)) {
			// Determine chat id.
			final String lStringChatId = lCommandWithArgs[1];
			try {
				lChatId = NumberUtils.parseNumber(lStringChatId, Long.class);
				lIsSameChat = aReceivedMessage.getChatId().equals(lChatId);
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
			lCommandText = (lIsStickerMode) ?
			        lLocalizationHelper.getLocalizedString("digestbot.error.sticker.format") :
			        lLocalizationHelper.getLocalizedString("digestbot.error.send.format");
		}

		if (lIsStickerMode && !lError) {
			aDigestBot.sendStickerToChat(lChatId,
			        (lIsSameChat) ? aReceivedMessage.getMessageId() : null, lCommandText.trim());
		} else {
			aDigestBot.sendMarkdownMessage(lChatId,
			        (lIsSameChat) ? aReceivedMessage.getMessageId() : null, lCommandText);
		}
	}
}
