package ru.exlmoto.digest.bot.ability;

import org.telegram.telegrambots.meta.api.objects.Message;

import ru.exlmoto.digest.bot.util.BotHelper;

public abstract class BotAbility {
	public void process(BotHelper helper, Message message) {
		new Thread(() -> processAux(helper, message)).start();
	}

	protected void processAux(BotHelper helper, Message message) {
		execute(helper, message);
	}

	protected abstract void execute(BotHelper helper, Message message);
}
