package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.KeyboardAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.repository.BotDigestRepository;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

@Component
public class DigestKeyboard extends KeyboardAbility {
	private final BotConfiguration config;
	private final BotDigestRepository digestRepository;

	public DigestKeyboard(BotConfiguration config, BotDigestRepository digestRepository) {
		this.config = config;
		this.digestRepository = digestRepository;
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return null;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, CallbackQuery entity) {

	}
}
