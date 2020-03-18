package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

@Component
public class HelpCommand extends MessageAbility {
	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, Message message) {
		long chatId = message.chat().id();
		User user = message.from();

		String answer = locale.i18n("bot.command.help");
		if (sender.isUserChatAdministrator(chatId, user.id())) {
			answer += "\n" + locale.i18n("bot.command.help.moder");
		}
		if (helper.isUserAdmin(user.username())) {
			answer += "\n" + locale.i18n("bot.command.help.admin");
		}

		sender.replyMarkdown(chatId, message.messageId(), answer);
	}
}
