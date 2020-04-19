/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ImageHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class SendCommand extends MessageAdminAbility {
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
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		long origChatId = message.chat().id();
		int origMessageId = message.messageId();
		String text = message.text();
		String[] commandWithArgs = text.split(" ");

		Command command = determineCommand(commandWithArgs[0]);

		Answer<String> commandRes = checkCommand(commandWithArgs, command, locale);
		if (!commandRes.ok()) {
			sender.replyMarkdown(origChatId, origMessageId, commandRes.error());
			return;
		}

		Answer<Long> chatIdRes = getChatId(commandWithArgs[1], locale);
		if (!chatIdRes.ok()) {
			sender.replyMarkdown(origChatId, origMessageId, chatIdRes.error());
			return;
		}

		long chatId = chatIdRes.answer();
		text = text.replaceFirst(commandWithArgs[0], "")
			.replaceFirst(commandWithArgs[1], "").trim();

		switch (command) {
			case send: {
				sender.sendSimpleToChat(chatId, text, origChatId, origMessageId);
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
					sender.replyMarkdown(origChatId, origMessageId,
						String.format(locale.i18n("bot.error.send.image"), text, chatId, imageRes.error()));
				}
				break;
			}
		}
	}

	protected String isolateCommand(String message) {
		int find = message.indexOf("@");
		if (find != -1) {
			return message.substring(0, find).substring(1);
		}
		return message.substring(1);
	}

	private Command determineCommand(String message) {
		String command = isolateCommand(message);
		try {
			return Command.valueOf(command);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong command: '%s', return default '%s'.", command, Command.send), iae);
		}
		return Command.send;
	}

	private Answer<String> checkCommand(String[] commandTokens, Command command, LocaleHelper locale) {
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

	private Answer<Long> getChatId(String chatId, LocaleHelper locale) {
		try {
			return Ok(NumberUtils.parseNumber(chatId, Long.class));
		} catch (NumberFormatException nfe) {
			return Error(String.format(locale.i18n("bot.error.chatid"), chatId));
		}
	}
}
