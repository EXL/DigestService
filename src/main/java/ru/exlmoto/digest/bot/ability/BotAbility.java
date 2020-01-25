package ru.exlmoto.digest.bot.ability;

import com.pengrad.telegrambot.model.Message;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

public abstract class BotAbility {
	public void process(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		new Thread(() -> processAux(helper, sender, locale, message)).start();
	}

	protected void processAux(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		execute(helper, sender, locale, message);
	}

	protected abstract void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message);
}
