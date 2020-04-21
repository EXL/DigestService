/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import ru.exlmoto.digest.bot.ability.message.MessageAdminAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class DeleteCommand extends MessageAdminAbility {
	private final Logger log = LoggerFactory.getLogger(DeleteCommand.class);

	private final DatabaseService service;

	public DeleteCommand(DatabaseService service) {
		this.service = service;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		String[] args = message.text().split(" ");
		String text = locale.i18n("bot.error.delete.format");

		if (args.length == 2) {
			long digestId = 0;
			try {
				digestId = NumberUtils.parseNumber(args[1], Long.class);
				service.deleteDigest(digestId);
				text = String.format(locale.i18n("bot.command.delete.ok"), digestId);
			} catch (NumberFormatException nfe) {
				log.warn(String.format("Cannot parse delete command argument: '%s' as Long.", args[1]), nfe);
				text = String.format(locale.i18n("bot.error.delete.id"), args[1]);
			} catch (DataAccessException dae) {
				log.error("Cannot delete BotDigestEntity object from database.", dae);
				text = String.format(locale.i18n("bot.error.delete.entry"), digestId, dae.getLocalizedMessage());
			}
		}

		sender.replyMarkdown(message.chat().id(), message.messageId(), text);
	}
}
