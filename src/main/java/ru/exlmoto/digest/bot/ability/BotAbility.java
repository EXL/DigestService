package ru.exlmoto.digest.bot.ability;

import org.springframework.scheduling.annotation.Async;

import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

public abstract class BotAbility<T> {
	@Async
	public void process(BotHelper helper, BotSender sender, LocaleHelper locale, T entity) {
		execute(helper, sender, locale, entity);
	}

	protected abstract void execute(BotHelper helper, BotSender sender, LocaleHelper locale, T entity);
}
