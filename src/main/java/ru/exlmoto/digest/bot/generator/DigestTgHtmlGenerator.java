package ru.exlmoto.digest.bot.generator;

import com.pengrad.telegrambot.model.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

@Component
public class DigestTgHtmlGenerator {
	private final FilterHelper filter;
	private final BotConfiguration config;
	private final BotHelper helper;
	private final LocalizationHelper locale;

	@Value("${general.date-format}")
	private String dateFormat;

	public DigestTgHtmlGenerator(FilterHelper filter,
	                             BotConfiguration config,
	                             BotHelper helper,
	                             LocalizationHelper locale) {
		this.filter = filter;
		this.config = config;
		this.helper = helper;
		this.locale = locale;
	}

	public String generateDigestMessageHtmlReport(Message message, String digest) {
		return
			String.format(locale.i18n("bot.hashtag.digest.subscribe.title"),
				helper.getValidChatName(message.chat())) + "\n\n<b>" +
			helper.getValidUsername(message.from()) + "</b> " + locale.i18n("bot.hashtag.digest.subscribe.wrote") +
			" (<i>" + filter.getDateFromTimeStamp(dateFormat, message.date()) + "</i>):\n<i>" +
			digest + "</i>\n\n" + locale.i18n("bot.hashtag.digest.subscribe.read") +
			" <a href=\"" + filter.checkLink(config.getMotofanChatUrl()) + message.messageId() + "\">" +
			locale.i18n("bot.hashtag.digest.subscribe.link") + "</a>";
	}
}
