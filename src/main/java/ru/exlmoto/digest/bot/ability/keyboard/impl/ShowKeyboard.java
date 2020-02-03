package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.User;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardPagerAbility;
import ru.exlmoto.digest.bot.sender.BotSender;

@Component
public class ShowKeyboard extends KeyboardPagerAbility {
	@Override
	protected Keyboard getKeyboard() {
		return Keyboard.show;
	}

	@Override
	public void handle(long chatId, int messageId, User user, int page, boolean edit, BotSender sender) {

	}
}
