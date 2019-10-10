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
		IMAGE_COMMAND,
		UNKNOWN_COMMAND
	}

	/**
	 * Send command looks like: "/send <chat id> <message>".
	 * Sticker command looks like: "/sticker <chat id> <sticker id>".
	 * Image command looks like: "/image <chat id> <image url>".
	 */
	@Override
	public void run(final DigestBot aDigestBot, final ReceivedMessage aReceivedMessage) {
		final LocalizationHelper lLocalizationHelper = aDigestBot.getLocalizationHelper();
		new Thread(() -> {
			String lCommandText = aReceivedMessage.getMessageText();
			Long lChatId = aReceivedMessage.getChatId();
			final String[] lCommandWithArgs = lCommandText.split(" ");
			boolean lIsSameChat = false;
			SendCommandMode lSendCommandMode = SendCommandMode.UNKNOWN_COMMAND;
			// boolean lIsStickerMode = false;
			boolean lError = false;
			if (lCommandWithArgs[0].startsWith(SEND_COMMAND)) {
				lSendCommandMode = SendCommandMode.SEND_COMMAND;
			} else if (lCommandWithArgs[0].startsWith(STICKER_COMMAND)) {
				lSendCommandMode = SendCommandMode.STICKER_COMMAND;
			} else if (lCommandWithArgs[0].startsWith(IMAGE_COMMAND)) {
				lSendCommandMode = SendCommandMode.IMAGE_COMMAND;
			}

			boolean lIsTextMode = (lSendCommandMode == SendCommandMode.SEND_COMMAND);

			if ((lIsTextMode && lCommandWithArgs.length >= 3) || (!lIsTextMode && lCommandWithArgs.length == 3)) {
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
				switch (lSendCommandMode) {
					default: {
						lCommandText = lLocalizationHelper.getLocalizedString("digestbot.error.send.format");
						break;
					}
					case STICKER_COMMAND: {
						lCommandText = lLocalizationHelper.getLocalizedString("digestbot.error.sticker.format");
						break;
					}
					case IMAGE_COMMAND: {
						lCommandText = lLocalizationHelper.getLocalizedString("digestbot.error.image.format");
					}
				}
			}

			lIsSameChat = aReceivedMessage.getChatId().equals(lChatId);
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
		}).start();
	}
}
