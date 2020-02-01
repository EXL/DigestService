package ru.exlmoto.digest.bot.generator;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class DigestTgHtmlGenerator {
	private final FilterTextHelper filterText;
	private final BotConfiguration config;
	private final BotHelper helper;
	private final LocalizationHelper locale;

	@Value("${general.date-format}")
	private String dateFormat;

	public DigestTgHtmlGenerator(FilterTextHelper filterText,
	                             BotConfiguration config,
	                             BotHelper helper,
	                             LocalizationHelper locale) {
		this.filterText = filterText;
		this.config = config;
		this.helper = helper;
		this.locale = locale;
	}

	public String generateDigestMessageHtmlReport(Message message, String digest) {
		return
			String.format(locale.i18n("bot.hashtag.digest.subscribe.title"),
				helper.getValidChatName(message.chat())) + "\n\n<b>" +
			helper.getValidUsername(message.from()) + "</b> " + locale.i18n("bot.hashtag.digest.subscribe.wrote") +
			" (" + filterText.getDateFromTimeStamp(dateFormat, message.date()) + "):\n<i>" +
			digest + "</i>\n\n" + locale.i18n("bot.hashtag.digest.subscribe.read") +
			" <a href=\"" + filterText.checkLink(config.getMotofanChatUrl()) + message.messageId() + "\">" +
			locale.i18n("bot.hashtag.digest.subscribe.link") + "</a>";
	}
}
