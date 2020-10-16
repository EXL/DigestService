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
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.worker.AvatarWorker;
import ru.exlmoto.digest.bot.worker.DigestWorker;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.entity.BotDigestUserEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.Optional;

@Component
public class DigestHashTag extends MessageAbility {
	private final Logger log = LoggerFactory.getLogger(DigestHashTag.class);

	private final BotConfiguration config;
	private final FilterHelper filter;
	private final DatabaseService service;
	private final AvatarWorker avatarWorker;
	private final DigestWorker digestWorker;

	public DigestHashTag(BotConfiguration config,
	                     FilterHelper filter,
	                     DatabaseService service,
	                     AvatarWorker avatarWorker,
	                     DigestWorker digestWorker) {
		this.config = config;
		this.filter = filter;
		this.service = service;
		this.avatarWorker = avatarWorker;
		this.digestWorker = digestWorker;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		long chatId = message.chat().id();
		int messageId = message.messageId();
		User user = message.from();
		long userId = user.id();

		String messageText = isolateMessageText(message.text());
		if (!messageText.isEmpty()) {
			if (messageText.length() <= config.getMaxDigestLength()) {
				try {
					BotDigestUserEntity digestUserEntity =
						service.getDigestUser(userId).orElseGet(() -> new BotDigestUserEntity(userId));
					digestUserEntity.setAvatar(avatarWorker.getAvatarLink(user));
					digestUserEntity.setUsername(helper.getValidUsername(user));
					service.saveDigestUser(digestUserEntity);

					Optional<BotDigestEntity> digestEntityOptional = service.getDigest(chatId, messageId);
					if (digestEntityOptional.isPresent()) {
						BotDigestEntity digestEntity = digestEntityOptional.get();
						digestEntity.setDigest(messageText);
						service.saveDigest(digestEntity);
						sender.replySimple(chatId, messageId, locale.i18nR("bot.hashtag.digest.changed"));
					} else {
						service.saveDigest(new BotDigestEntity(chatId,
							message.date(), (long) messageId, messageText, digestUserEntity));
						sender.replySimple(chatId, messageId,
							locale.i18nRU("bot.hashtag.digest.ok", helper.getValidUsername(user)));
						if (chatId == config.getMotofanChatId()) {
							digestWorker.sendDigestToSubscribers(sender, message, messageText);
						}
					}
				} catch (DataAccessException dae) {
					log.error("Cannot save digest entity to database.", dae);
					sender.replyMarkdown(chatId, messageId,
						String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage()));
				}
			} else {
				sender.replySimple(chatId, messageId, locale.i18n("bot.hashtag.digest.error.length"));
			}
		} else {
			sender.replySimple(chatId, messageId, locale.i18n("bot.hashtag.digest.error.empty"));
		}
	}

	protected String isolateMessageText(String message) {
		return filter.strip(filter.removeHtmlTags(message.replaceAll("#digest|#news", "")));
	}
}
