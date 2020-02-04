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

import static com.pengrad.telegrambot.model.Chat.Type.Private;

@Component
public class DigestKeyboard extends KeyboardPagerAbility {
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

	@Override
	protected Keyboard getKeyboard() {
		return Keyboard.digest;
	}

	@Override
	protected boolean handleQuery(String callbackId, User user, int page, BotSender sender, BotHelper helper) {
		return sendCallbackQueryPage(callbackId, locale, page, sender);
	}

	@Override
	public void handle(int messageId, Chat chat, User user, int page, boolean edit, BotSender sender) {
		int NEW_MARKERS_COUNT = 3;
		long chatId = chat.id();
		String username = helper.getValidUsername(user);
		String text = locale.i18nRU("bot.command.digest.empty", username);

		Page<BotDigestEntity> digestEntities = null;
		int totalPages = 0;
		long totalEntries;
		try {
			digestEntities = digestRepository.findBotDigestEntitiesByChat(PageRequest.of(page - 1,
				config.getDigestPagePosts(), Sort.by(Sort.Order.desc("id"))), chatId);
		} catch (DataAccessException dae) {
			log.error("Cannot get BotDigestEntity objects from database.", dae);
			text = String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage());
		}
		if (digestEntities != null && !digestEntities.isEmpty()) {
			totalPages = digestEntities.getTotalPages();
			totalEntries = digestEntities.getTotalElements();

			text = "<i>";
			if (chat.type().equals(Private)) {
				text += locale.i18nU("bot.command.digest.private", username) + "\n";
			} else {
				text += locale.i18nRU("bot.command.digest.hello", username) + "\n";
				text += locale.i18nR("bot.command.digest.header") + "\n";
			}
			text += String.format(locale.i18n("bot.info.digest.stats"), totalEntries, page, totalPages);
			text += "</i>\n\n";

			String marker = locale.i18n("bot.command.digest.marker");
			String newMarker = locale.i18n("bot.command.digest.marker.new");

			int newMarkerCount = NEW_MARKERS_COUNT;
			StringBuilder stringBuilder = new StringBuilder();
			for (BotDigestEntity entity : digestEntities) {
				stringBuilder.append(marker).append(" ");
				if (page == 1 && newMarkerCount > 0) {
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
			sender.editHtmlMessage(chatId, messageId, text, getMarkup(locale, config, page, totalPages));
		} else {
			sender.replyHtmlMessage(chatId, messageId, text, getMarkup(locale, config, page, totalPages));
		}
	}
}
