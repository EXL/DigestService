package ru.exlmoto.digestbot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

import ru.exlmoto.digestbot.DigestBot;

public interface BotCommand {
	void run(DigestBot aDigestBot, Update aUpdate);
}
