package ru.exlmoto.digestbot.commands.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotAdminCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

@Component
public class SendCommand extends BotAdminCommand {
	@Override
	public void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage) {
		String lCommandText = aReceivedMessage.getMessageText();
		Long lChatId = aReceivedMessage.getChatId();
		// Send command looks like: "/send <chat id> <message>".
		final String[] lCommandWithArgs = lCommandText.split(" ");
		if (lCommandWithArgs.length >= 3) {
			// Determine chat id and cut message from command.
			final String lStringChatId = lCommandWithArgs[1];
			lChatId = NumberUtils.parseNumber(lStringChatId, Long.class);
			lCommandText = lCommandText.replaceFirst("/send", "")
			                           .replaceFirst(lStringChatId, "");
		} else {
			// TODO: Set a normal error message.
			lCommandText = "Error!";
		}
		aDigestBot.sendSimpleMessage(lChatId, lCommandText);
	}
}
