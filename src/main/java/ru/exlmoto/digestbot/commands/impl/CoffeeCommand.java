package ru.exlmoto.digestbot.commands.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digestbot.DigestBot;
import ru.exlmoto.digestbot.commands.BotCommand;
import ru.exlmoto.digestbot.utils.ReceivedMessage;
import ru.exlmoto.digestbot.yaml.YamlLocalizationHelper;

@Component
public class CoffeeCommand extends BotCommand {
	private final String mCoffeeStickerId;

	public CoffeeCommand(@Value("${digestbot.sticker.coffee}") final String aCoffeeStickerId) {
		mCoffeeStickerId = aCoffeeStickerId;
	}

	@Override
	public void run(final DigestBot aDigestBot,
	                final YamlLocalizationHelper aLocalizationHelper,
	                final ReceivedMessage aReceivedMessage) {
		aDigestBot.sendStickerToChat(aReceivedMessage.getChatId(),
			aReceivedMessage.getMessageId(),
			aReceivedMessage.getChatId(),
			mCoffeeStickerId);
	}
}
