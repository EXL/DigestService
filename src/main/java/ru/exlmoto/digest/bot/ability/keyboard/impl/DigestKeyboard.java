package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import org.thymeleaf.util.ArrayUtils;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

@Component
public class DigestKeyboard extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(DigestKeyboard.class);

	private final BotConfiguration config;
	private final LocaleHelper locale;
	private final BotDigestRepository digestRepository;

	public DigestKeyboard(BotConfiguration config, LocaleHelper locale, BotDigestRepository digestRepository) {
		this.config = config;
		this.locale = locale;
		this.digestRepository = digestRepository;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return getMarkupPager(1);
	}

	/*
	 * Intelligent pager from https://lab.exlmoto.ru/digests page ported for Telegram.
	 * Source: https://github.com/EXL/DigestBot/blob/master/Stuff/DigestHistorySite/index.php#L50
	 */
	protected InlineKeyboardMarkup getMarkupPager(int page) {
		int all = 0;
		try {
			all = (int) ((digestRepository.count() - 1) / config.getDigestPagePosts());
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest Entities count.", dae);
		}
		if (all < 1) {
			return null;
		}

		all++;
		int start = page - 1;
		if (start < 0) {
			start = 0;
		}

		int end = page;
		if (end > all) {
			end = all;
		}

		/* Patched for one width. */
		if (page == 1) {
			end += 2;
		} else if (page == 2) {
			end += 1;
			start -= 1;
		}
		if (page == all) {
			start -= 2;
		} else if (page == all - 1) {
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
		if (page < all - 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.right"))
				.callbackData(callbackPageData(page + 1)));
		}
		if (end < all - 1) {
			keyboardRow.add(new InlineKeyboardButton(locale.i18n("bot.command.digest.pager.right.last"))
				.callbackData(callbackPageData(all)));
		}

//		int size = keyboardRow.size();
//		InlineKeyboardButton[] buttons = new InlineKeyboardButton[size];
//		for (int i = 0; i < size; i++) {
//			buttons[i] = keyboardRow.get(i);
//		}

		return new InlineKeyboardMarkup((InlineKeyboardButton[]) ArrayUtils.toArray(keyboardRow));
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		int messageId = message.messageId();
		Chat chat = message.chat();
		long chatId = chat.id();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLeft(Keyboard.chopKeyboardNameLeft(callback.data()));

		int page = 0;
		try {
			page = NumberUtils.parseNumber(key, Integer.class);
		} catch (NumberFormatException nfe) {
			log.warn(String.format("Cannot parse inline page key: '%s' as Integer.", key), nfe);
		}

		sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.digest.page") + " " + page);

		sender.editMessage(chatId, messageId, "TEST", getMarkupPager(page));
	}


	public void processDigestMessage(long chatId, int messageId, Chat chat, boolean edit, BotSender sender) {

	}

	private String callbackPageData(int page) {
		return Keyboard.digest.withName() + Keyboard.PAGE + page;
	}
}
