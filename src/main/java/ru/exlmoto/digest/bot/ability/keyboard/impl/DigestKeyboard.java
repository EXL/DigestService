package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotDigestEntity;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.filter.FilterHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class DigestKeyboard extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(DigestKeyboard.class);

	private final BotConfiguration config;
	private final FilterHelper filter;
	private final LocaleHelper locale;
	private final BotHelper helper;
	private final BotDigestRepository digestRepository;

	public DigestKeyboard(BotConfiguration config,
	                      FilterHelper filter,
	                      LocaleHelper locale,
	                      BotHelper helper,
	                      BotDigestRepository digestRepository) {
		this.config = config;
		this.filter = filter;
		this.locale = locale;
		this.helper = helper;
		this.digestRepository = digestRepository;
	}

	/*
	 * Intelligent pager from https://lab.exlmoto.ru/digests page ported for Telegram.
	 * Source: https://github.com/EXL/DigestBot/blob/master/Stuff/DigestHistorySite/index.php#L50
	 */
	@Override
	public InlineKeyboardMarkup getMarkup(int page, int totalPages) {
		if (totalPages <= 1) {
			return null;
		}

		int start = page - 1;
		if (start < 0) {
			start = 0;
		}

		int end = page;
		if (end > totalPages) {
			end = totalPages;
		}

		/* Patched for one width. */
		if (page == 1) {
			end += 2;
		} else if (page == 2) {
			end += 1;
			start -= 1;
		}
		if (page == totalPages) {
			start -= 2;
		} else if (page == totalPages - 1) {
			start -= 1;
			end += 1;
		}

		List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
		if (start > 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.left.first"))
				.callbackData(callbackPageData(1)));
		}
		if (page > 2) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.left"))
				.callbackData(callbackPageData(page - 1)));
		}
		for (int i = start; i < end; i++) {
			keyboardRow.add(new InlineKeyboardButton(
				(i == page - 1) ?
					"|" + (i + 1) + "|" :
					String.valueOf(i + 1)
			).callbackData(callbackPageData(i + 1)));
		}
		if (page < totalPages - 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.right"))
				.callbackData(callbackPageData(page + 1)));
		}
		if (end < totalPages - 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.right.last"))
				.callbackData(callbackPageData(totalPages)));
		}
		return new InlineKeyboardMarkup((InlineKeyboardButton[]) ArrayUtils.toArray(keyboardRow));
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		int messageId = message.messageId();
		long chatId = message.chat().id();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLeft(Keyboard.chopKeyboardNameLeft(callback.data()));

		int page = 1;
		try {
			page = NumberUtils.parseNumber(key, Integer.class);
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot parse inline page key: '%s' as Integer.", key), nfe);
		}

		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.digest.page") + " " + page);

		processDigestMessage(chatId, messageId, callback.from(), page - 1, true, sender);
	}


	public void processDigestMessage(long chatId, int messageId, User user, int page, boolean edit, BotSender sender) {
		int NEW_MARKERS_COUNT = 3;
		String username = helper.getValidUsername(user);
		String text = locale.i18nRU("bot.command.digest.empty", username);

		Page<BotDigestEntity> digestEntities = null;
		int totalPages = 0;
		try {
			digestEntities = digestRepository.findBotDigestEntitiesByChat(PageRequest.of(page,
				config.getDigestPagePosts(), Sort.by(Sort.Order.desc("id"))), chatId);
		} catch (DataAccessException dae) {
			log.error("Cannot get BotDigestEntity objects from database.", dae);
			text = String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
		if (digestEntities != null && !digestEntities.isEmpty()) {
			totalPages = digestEntities.getTotalPages();

			text = locale.i18nRU("bot.command.digest.hello", username) + "\n";
			text += locale.i18nR("bot.command.digest.header") + " " + (page + 1) + "/" + totalPages + ":\n";

			String marker = locale.i18n("bot.command.digest.marker");
			String newMarker = locale.i18n("bot.command.digest.marker.new");

			int newMarkerCount = NEW_MARKERS_COUNT;
			StringBuilder stringBuilder = new StringBuilder();
			for (BotDigestEntity entity : digestEntities) {
				stringBuilder.append(marker).append(" ");
				if (page == 0 && newMarkerCount > 0) {
					stringBuilder.append(newMarker).append(" ");
				}
				stringBuilder.append(entity.getDigest());
				long messageEntityId = entity.getMessageId();
				if (chatId == config.getMotofanChatId() && messageEntityId != 0L) {
					stringBuilder.append(" <a href=\"").append(filter.checkLink(config.getMotofanChatUrl()))
						.append(messageEntityId).append("\">").append(locale.i18n("bot.command.digest.link"))
						.append("</a>");
				}
				stringBuilder.append("\n");
				newMarkerCount--;
			}
			text += stringBuilder.toString();
		}
		if (edit) {
			sender.editHtmlMessage(chatId, messageId, text, getMarkup(page + 1, totalPages));
		} else {
			sender.replyHtmlMessage(chatId, messageId, text, getMarkup(page + 1, totalPages));
		}
	}

	private String callbackPageData(int page) {
		return Keyboard.digest.withName() + Keyboard.PAGE + page;
	}
}
