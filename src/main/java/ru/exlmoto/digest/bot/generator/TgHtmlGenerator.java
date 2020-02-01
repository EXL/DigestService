package ru.exlmoto.digest.bot.generator;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

@Component
public class TgHtmlGenerator {
	private final FilterTextHelper filterText;
	private final BotConfiguration config;
	private final BotHelper helper;
	private final LocalizationHelper locale;

	@Value("${general.date-format}")
	private String dateFormat;

	public TgHtmlGenerator(FilterTextHelper filterText,
	                       BotConfiguration config,
	                       BotHelper helper,
	                       LocalizationHelper locale) {
		this.filterText = filterText;
		this.config = config;
		this.helper = helper;
		this.locale = locale;
	}

	public String generateDigestMessageHtmlReport(Chat chat, User user, int messageId, long timeStamp, String digest) {
		return
			String.format(locale.i18n("bot.hashtag.digest.subscribe.title"), helper.getValidChatName(chat)) +
			"\n\n<b>" + helper.getValidUsername(user) + "</b>" +
			locale.i18n("bot.hashtag.digest.subscribe.wrote") + " (" +
			filterText.getDateFromTimeStamp(dateFormat, timeStamp) + "):\n<i>" + digest + "</i>\n\n" +
			locale.i18n("bot.hashtag.digest.subscribe.read") +
			" <a href=\"" + filterText.checkLink(config.getMotofanChatUrl()) + messageId + "\">" +
			locale.i18n("bot.hashtag.digest.subscribe.link") + "</a>";
	}
}
