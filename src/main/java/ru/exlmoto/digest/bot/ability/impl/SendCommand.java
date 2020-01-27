package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digest.bot.ability.BotAbilityAdmin;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class SendCommand extends BotAbilityAdmin {
	private final Logger log = LoggerFactory.getLogger(SendCommand.class);

	private enum Command {
		send,
		sticker,
		image
	}

	private final ImageHelper rest;

	public SendCommand(ImageHelper rest) {
		this.rest = rest;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		long origChatId = message.chat().id();
		int origMessageId = message.messageId();
		String text = message.text();
		String[] commandWithArgs = text.split(" ");

		Command command = determineCommand(commandWithArgs[0]);

		Answer<String> commandRes = checkCommand(commandWithArgs, command, locale);
		if (!commandRes.ok()) {
			sender.replyMessage(origChatId, origMessageId, commandRes.error());
			return;
		}

		Answer<Long> chatIdRes = getChatId(commandWithArgs[1], locale);
		if (!chatIdRes.ok()) {
			sender.replyMessage(origChatId, origMessageId, chatIdRes.error());
			return;
		}

		long chatId = chatIdRes.answer();
		text = text.replaceFirst(commandWithArgs[0], "")
			.replaceFirst(commandWithArgs[1], "").trim();

		switch (command) {
			case send: {
				sender.sendMessageToChat(chatId, text, origChatId, origMessageId);
				break;
			}
			case sticker: {
				sender.sendStickerToChat(chatId, text, origChatId, origMessageId);
				break;
			}
			case image: {
				Answer<String> imageRes = rest.getImageByLink(text);
				if (imageRes.ok()) {
					sender.sendPhotoToChat(chatId, imageRes.answer(), origChatId, origMessageId);
				} else {
					sender.replyMessage(origChatId, origMessageId,
						String.format(locale.i18n("bot.error.send.image"), text, chatId, imageRes.error()));
				}
				break;
			}
		}
	}

	private Command determineCommand(String message) {
		String command = message.substring(1);
		try {
			return Command.valueOf(command);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong command: '%s', return first default '%s'.", command, Command.send), iae);
			return Command.send;
		}
	}

	private Answer<String> checkCommand(String[] commandTokens, Command command, LocalizationHelper locale) {
		boolean isTextMode = (command == Command.send);
		if ((isTextMode && commandTokens.length < 3) || (!isTextMode && commandTokens.length != 3)) {
			switch (command) {
				case send: return Error(locale.i18n("bot.error.send.format"));
				case sticker: return Error(locale.i18n("bot.error.sticker.format"));
				case image: return Error(locale.i18n("bot.error.image.format"));
			}
		}
		return Ok("Ok!");
	}

	private Answer<Long> getChatId(String chatId, LocalizationHelper locale) {
		try {
			return Ok(NumberUtils.parseNumber(chatId, Long.class));
		} catch (NumberFormatException nfe) {
			return Error(locale.i18n("bot.error.chatid"));
		}
	}
}
