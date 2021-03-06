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

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardPagerAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class ShowKeyboard extends KeyboardPagerAbility {
	private final Logger log = LoggerFactory.getLogger(ShowKeyboard.class);

	private final BotConfiguration config;
	private final LocaleHelper locale;
	private final FilterHelper filter;
	private final DatabaseService service;

	public ShowKeyboard(BotConfiguration config,
	                    LocaleHelper locale,
	                    FilterHelper filter,
	                    DatabaseService service) {
		this.config = config;
		this.locale = locale;
		this.filter = filter;
		this.service = service;
	}

	@Override
	protected Keyboard getKeyboard() {
		return Keyboard.show;
	}

	@Override
	protected boolean handleQuery(String callbackId, User user, int page, BotSender sender, BotHelper helper) {
		if (helper.isUserAdmin(user.username())) {
			return sendCallbackQueryPage(callbackId, locale, page, sender);
		}
		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.show.admin"));
		return false;
	}

	@Override
	public void handle(int messageId, Chat chat, User user, int page, boolean edit, BotSender sender) {
		String text = locale.i18n("bot.error.show.empty");
		int totalPages = 0;
		long totalEntries;

		Page<BotDigestEntity> digestEntities = null;
		try {
			digestEntities = service.getAllDigests(page, config.getShowPagePosts());
		} catch (DataAccessException dae) {
			log.error("Cannot get BotDigestEntity objects from database.", dae);
			text = String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
		if (digestEntities != null && !digestEntities.isEmpty()) {
			totalPages = digestEntities.getTotalPages();
			totalEntries = digestEntities.getTotalElements();
			text = generateShowReport(digestEntities, locale.i18n("bot.command.show.header") + " " +
					String.format(locale.i18n("bot.info.digest.stats"), totalEntries, page, totalPages));
		}

		if (edit) {
			sender.editMarkdown(chat.id(), messageId, text, getMarkup(locale, config, page, totalPages));
		} else {
			sender.replyMarkdown(chat.id(), messageId, text, getMarkup(locale, config, page, totalPages));
		}
	}

	protected String generateShowReport(Page<BotDigestEntity> entities, String header) {
		final int CHOP_NUMBER = 6;
		final int CHOP_USER = 7;
		final int CHOP_DIGEST = 25;

		StringBuilder builder = new StringBuilder(header);
		builder.append("\n\n");
		builder.append("```\n");
		builder.append(filter.arrangeString("id", CHOP_NUMBER)).append(" ");
		builder.append(filter.arrangeString("user", CHOP_USER)).append(" ");
		builder.append(filter.arrangeString("chat", CHOP_NUMBER)).append(" ");
		builder.append("post").append("\n");

		for (int i = 0; i < CHOP_NUMBER * 2 + CHOP_USER + CHOP_DIGEST + 3; ++i) {
			builder.append("-");
		}
		builder.append("\n");

		for (BotDigestEntity entity : entities) {
			builder.append(filter.ellipsisLeftA(String.valueOf(entity.getId()), CHOP_NUMBER)).append(" ");
			builder.append(filter.ellipsisRightA(entity.getUser().getUsername(), CHOP_USER)).append(" ");
			builder.append(filter.ellipsisLeftA(String.valueOf(entity.getChat()), CHOP_NUMBER)).append(" ");
			builder.append(filter.strip(filter.ellipsisRightA(entity.getDigest(), CHOP_DIGEST))).append("\n");
		}
		builder.append("\n```");

		return builder.toString();
	}
}
