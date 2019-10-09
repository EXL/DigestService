package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;

@Component
public class CoffeeCommand extends BotCommand {
	private final String mCoffeeStickerId;

	public CoffeeCommand(@Value("${digestbot.sticker.coffee}") String aCoffeeStickerId) {
		mCoffeeStickerId = aCoffeeStickerId;
	}

	@Override
	public void run(DigestBot aDigestBot, ReceivedMessage aReceivedMessage) {
		aDigestBot.sendStickerToChat(aReceivedMessage.getChatId(), mCoffeeStickerId);
	}
}
