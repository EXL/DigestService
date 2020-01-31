package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class DigestHashTag extends MessageAbility {
	private final BotConfiguration config;
	private final FilterTextHelper filterText;

	public DigestHashTag(BotConfiguration config, FilterTextHelper filterText) {
		this.config = config;
		this.filterText = filterText;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		long chatId = message.chat().id();
		String messageText = isolateMessageText(message.text());
		if (!messageText.isEmpty()) {
			sender.replyMessage(chatId, message.messageId(),
				locale.i18nRU("bot.hashtag.digest.ok", helper.getValidUsername(message.from())));

			// Commit digest to DB
			// Commit user to DB
			if (chatId == config.getMotofanChatId()) {
				// Sends message to subs
			}
		}
	}



	protected String isolateMessageText(String message) {
		return filterText.removeHtmlTags(message.replaceAll("#digest|#news", "")).trim();
	}
}
