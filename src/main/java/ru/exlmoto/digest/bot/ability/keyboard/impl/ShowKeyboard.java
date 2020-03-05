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
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class ShowKeyboard extends KeyboardPagerAbility {
	private final Logger log = LoggerFactory.getLogger(ShowKeyboard.class);

	private final BotConfiguration config;
	private final LocaleHelper locale;
	private final FilterHelper filter;
	private final BotDigestRepository digestRepository;

	public ShowKeyboard(BotConfiguration config,
	                    LocaleHelper locale,
	                    FilterHelper filter,
	                    BotDigestRepository digestRepository) {
		this.config = config;
		this.locale = locale;
		this.filter = filter;
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

		StringBuilder stringBuilder = new StringBuilder(header);
		stringBuilder.append("\n\n");
		stringBuilder.append("```\n");
		stringBuilder.append(filter.arrangeString("id", CHOP_NUMBER)).append(" ");
		stringBuilder.append(filter.arrangeString("user", CHOP_USER)).append(" ");
		stringBuilder.append(filter.arrangeString("chat", CHOP_NUMBER)).append(" ");
		stringBuilder.append("post").append("\n");

		for (int i = 0; i < CHOP_NUMBER * 2 + CHOP_USER + CHOP_DIGEST + 3; ++i) {
			stringBuilder.append("-");
		}
		stringBuilder.append("\n");

		for (BotDigestEntity entity : entities) {
			stringBuilder.append(filter.ellipsisLeftA(String.valueOf(entity.getId()), CHOP_NUMBER)).append(" ");
			stringBuilder.append(filter.ellipsisRightA(entity.getUser().getUsername(), CHOP_USER)).append(" ");
			stringBuilder.append(filter.ellipsisLeftA(String.valueOf(entity.getChat()), CHOP_NUMBER)).append(" ");
			stringBuilder.append(filter.ellipsisRightA(entity.getDigest(), CHOP_DIGEST)).append("\n");
		}
		stringBuilder.append("\n```");

		return stringBuilder.toString();
	}
}
