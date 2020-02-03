package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class DigestKeyboard extends KeyboardAbility {
	private final Logger log = LoggerFactory.getLogger(DigestKeyboard.class);

	private final BotConfiguration config;
	private final LocalizationHelper locale;
	private final BotDigestRepository digestRepository;

	public DigestKeyboard(BotConfiguration config, LocalizationHelper locale, BotDigestRepository digestRepository) {
		this.config = config;
		this.locale = locale;
		this.digestRepository = digestRepository;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		int pagesCount = config.getDigestPageCount();

		int buttonsCount;
		try {
			buttonsCount = (int) (((digestRepository.count() - 1) / pagesCount) + 1);
		} catch (DataAccessException dae) {
			log.error("Cannot get Digest Entities count.", dae);
			buttonsCount = 0;
		}

		if (buttonsCount <= 1) {
			return null;
		}
		if (buttonsCount > pagesCount) {
			buttonsCount = pagesCount;
		}

		InlineKeyboardButton[] keyboardRow = new InlineKeyboardButton[buttonsCount];
		for (int i = 0; i < buttonsCount; i++) {
			keyboardRow[i] = new InlineKeyboardButton(locale.i18n("bot.command.digest.page") + " " + (i + 1))
				.callbackData(Keyboard.digest.withName() + Keyboard.PAGE + i);
		}
		return new InlineKeyboardMarkup(keyboardRow);
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery entity) {

	}
}
