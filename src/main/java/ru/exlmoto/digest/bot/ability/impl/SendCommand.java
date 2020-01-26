package ru.exlmoto.digest.bot.ability.impl;

import com.pengrad.telegrambot.model.Message;

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
	private final ImageHelper rest;

	private enum Command {
		SEND("/send"),
		STICKER("/sticker"),
		IMAGE("/image");

		private final String command;

		Command(String command) {
			this.command = command;
		}

		public String command() {
			return command;
		}
	}

	public SendCommand(ImageHelper rest) {
		this.rest = rest;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		long origChatId = message.chat().id();
		int origMessageId = message.messageId();
		String text = message.text();
		String[] commandWithArgs = text.split(" ");

		Command command = determineCommand(text);

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
			case SEND: {
				sender.sendMessageToChat(chatId, text, origChatId, origMessageId);
				break;
			}
			case STICKER: {
				sender.sendStickerToChat(chatId, text, origChatId, origMessageId);
				break;
			}
			case IMAGE: {
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
		if (message.startsWith(Command.STICKER.command())) {
			return Command.STICKER;
		} else if (message.startsWith(Command.IMAGE.command())) {
			return Command.IMAGE;
		} else {
			return Command.SEND;
		}
	}

	private Answer<String> checkCommand(String[] commandTokens, Command command, LocalizationHelper locale) {
		boolean isTextMode = (command == Command.SEND);
		if ((isTextMode && commandTokens.length < 3) || (!isTextMode && commandTokens.length != 3)) {
			switch (command) {
				case SEND: return Error(locale.i18n("bot.error.send.format"));
				case STICKER: return Error(locale.i18n("bot.error.sticker.format"));
				case IMAGE: return Error(locale.i18n("bot.error.image.format"));
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
