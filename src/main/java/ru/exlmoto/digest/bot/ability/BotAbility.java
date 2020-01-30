package ru.exlmoto.digest.bot.ability;

import com.pengrad.telegrambot.model.Message;

import org.springframework.scheduling.annotation.Async;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

public abstract class BotAbility {
	@Async
	public void process(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		processAux(helper, sender, locale, message);
	}

	protected void processAux(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		execute(helper, sender, locale, message);
	}

	protected abstract void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message);
}
