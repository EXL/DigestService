package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

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
		String lCommandText = aReceivedMessage.getMessageText();
		Long lChatId = aReceivedMessage.getChatId();
		final String[] lCommandWithArgs = lCommandText.split(" ");

		boolean lIsStickerMode = false;
		boolean lError = false;
		String lCommand = SEND_COMMAND;
		if (lCommandWithArgs[0].trim().equals(STICKER_COMMAND)) {
			lCommand = STICKER_COMMAND;
			lIsStickerMode = true;
		}

		if ((!lIsStickerMode && lCommandWithArgs.length >= 3) || (lIsStickerMode && lCommandWithArgs.length == 3)) {
			// Determine chat id and cut message from command.
			final String lStringChatId = lCommandWithArgs[1];
			try {
				lChatId = NumberUtils.parseNumber(lStringChatId, Long.class);
			} catch (NumberFormatException e) {
				// TODO: Set a normal error message.
				lError = true;
				lCommandText = "NumberFormatException Error!";
			}
			if (!lError) {
				lCommandText = lCommandText.replaceFirst(lCommand, "")
				        .replaceFirst(lStringChatId, "");
			}
		} else {
			// TODO: Set a normal error message.
			lError = true;
			lCommandText = "Command Format Error!";
		}

		if (lIsStickerMode && !lError) {
			aDigestBot.sendStickerToChat(lChatId, aReceivedMessage.getMessageId(), lCommandText.trim());
		} else {
			aDigestBot.sendSimpleMessage(lChatId, aReceivedMessage.getMessageId(), lCommandText);
		}
	}
}
