package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardPagerAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class ShowKeyboard extends KeyboardPagerAbility {
	private final Logger log = LoggerFactory.getLogger(ShowKeyboard.class);

	private final BotConfiguration config;
	private final LocaleHelper locale;
	private final BotDigestRepository digestRepository;

	public ShowKeyboard(BotConfiguration config, LocaleHelper locale, BotDigestRepository digestRepository) {
		this.config = config;
		this.locale = locale;
		this.digestRepository = digestRepository;
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
			digestEntities = digestRepository.findAll(PageRequest.of(page - 1,
				config.getShowPagePosts(), Sort.by(Sort.Order.desc("id"))));
		} catch (DataAccessException dae) {
			log.error("Cannot get BotDigestEntity objects from database.", dae);
			text = String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
		if (digestEntities != null && !digestEntities.isEmpty()) {
			totalPages = digestEntities.getTotalPages();
			totalEntries = digestEntities.getTotalElements();
			text = generateShowReport(digestEntities, locale,
				locale.i18n("bot.command.show.header") + " " +
					String.format(locale.i18n("bot.info.digest.stats"), totalEntries, page, totalPages));
		}

		if (edit) {
			sender.editMarkdown(chat.id(), messageId, text, getMarkup(locale, config, page, totalPages));
		} else {
			sender.replyMarkdown(chat.id(), messageId, text, getMarkup(locale, config, page, totalPages));
		}
	}

	protected String generateShowReport(Page<BotDigestEntity> entities, LocaleHelper locale, String header) {
		final int CHOP_NUMBER = 5;
		final int CHOP_USER = 7;
		final int CHOP_DIGEST = 25;
		String ellipsis = locale.i18n("bot.command.show.ellipsis");

		StringBuilder stringBuilder = new StringBuilder(header);
		stringBuilder.append("\n\n");
		stringBuilder.append("```\n");
		stringBuilder.append(arrangeString("id", CHOP_NUMBER)).append(" ");
		stringBuilder.append(arrangeString("user", CHOP_USER)).append(" ");
		stringBuilder.append(arrangeString("chat", CHOP_NUMBER)).append(" ");
		stringBuilder.append("post").append("\n");
		stringBuilder.append("---------------------------------------------\n");
		for (BotDigestEntity entity : entities) {
			stringBuilder.append(ellipsisString(String.valueOf(entity.getId()),
				CHOP_NUMBER, ellipsis, false)).append(" ");
			stringBuilder.append(ellipsisString(entity.getUser().getUsername(),
				CHOP_USER, ellipsis, true)).append(" ");
			stringBuilder.append(ellipsisString(String.valueOf(entity.getChat()),
				CHOP_NUMBER, ellipsis, false)).append(" ");
			stringBuilder.append(ellipsisString(entity.getDigest(),
				CHOP_DIGEST, ellipsis, true)).append("\n");
		}
		stringBuilder.append("\n```");
		return stringBuilder.toString();
	}

	protected String arrangeString(String string, int length) {
		int stringLength = string.length();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length - stringLength; ++i) {
			stringBuilder.append(' ');
		}
		return string + stringBuilder.toString();
	}

	protected String ellipsisString(String string, int length, String ellipsis, boolean right) {
		if (length > 0) {
			int stringLength = string.length();
			if (stringLength < length) {
				return arrangeString(string, length);
			}
			return (right) ?
				string.substring(0, length - 1) + ellipsis :
				ellipsis + string.substring(stringLength - length + 1);
		}
		return string;
	}
}