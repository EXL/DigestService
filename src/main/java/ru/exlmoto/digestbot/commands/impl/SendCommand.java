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
	private final String IMAGE_COMMAND = "/image";

	private enum SendCommandMode {
		SEND_COMMAND,
		STICKER_COMMAND,
		IMAGE_COMMAND
	}

	/**
	 * Send command looks like: "/send <chat id> <message>".
	 * Sticker command looks like: "/sticker <chat id> <sticker id>".
	 * Image command looks like: "/image <chat id> <image url>".
	 */
	@Override
	public void run(final DigestBot aDigestBot,
	                final LocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		String lCommandText = aReceivedMessage.getMessageText();
		Long lChatId = aReceivedMessage.getChatId();
		final String[] lCommandWithArgs = lCommandText.split(" ");

		SendCommandMode lSendCommandMode = SendCommandMode.SEND_COMMAND;
		if (lCommandWithArgs[0].startsWith(STICKER_COMMAND)) {
			lSendCommandMode = SendCommandMode.STICKER_COMMAND;
		} else if (lCommandWithArgs[0].startsWith(IMAGE_COMMAND)) {
			lSendCommandMode = SendCommandMode.IMAGE_COMMAND;
		}

		boolean lError = false;
		boolean lIsTextMode = (lSendCommandMode == SendCommandMode.SEND_COMMAND);

		if ((lIsTextMode && lCommandWithArgs.length >= 3) || (!lIsTextMode && lCommandWithArgs.length == 3)) {
			// Determine chat id.
			final String lStringChatId = lCommandWithArgs[1];
			try {
				lChatId = NumberUtils.parseNumber(lStringChatId, Long.class);
			} catch (NumberFormatException e) {
				lError = true;
				lCommandText = aLocalizationHelper.getLocalizedString("digestbot.error.chatid");
			}
			if (!lError) {
				// Delete command and chat id from received message.
				lCommandText = lCommandText.replaceFirst(lCommandWithArgs[0], "")
					               .replaceFirst(lCommandWithArgs[1], "");
			}
		} else {
			lError = true;
			switch (lSendCommandMode) {
				default: {
					lCommandText = aLocalizationHelper.getLocalizedString("digestbot.error.send.format");
					break;
				}
				case STICKER_COMMAND: {
					lCommandText = aLocalizationHelper.getLocalizedString("digestbot.error.sticker.format");
					break;
				}
				case IMAGE_COMMAND: {
					lCommandText = aLocalizationHelper.getLocalizedString("digestbot.error.image.format");
					break;
				}
			}
		}

		boolean lIsSameChat = aReceivedMessage.getChatId().equals(lChatId);
		if (lError) {
			lSendCommandMode = SendCommandMode.SEND_COMMAND;
		}

		switch (lSendCommandMode) {
			default: {
				aDigestBot.sendMarkdownMessage(lChatId,
					(lIsSameChat) ? aReceivedMessage.getMessageId() : null, lCommandText);
				break;
			}
			case STICKER_COMMAND: {
				aDigestBot.sendStickerToChat(lChatId,
					(lIsSameChat) ? aReceivedMessage.getMessageId() : null,
					aReceivedMessage.getChatId(), lCommandText.trim());
				break;
			}
			case IMAGE_COMMAND: {
				aDigestBot.sendPhotoToChatFromUrl(lChatId,
					(lIsSameChat) ? aReceivedMessage.getMessageId() : null,
					aReceivedMessage.getChatId(), null, lCommandText.trim());
				break;
			}
		}
	}
}
