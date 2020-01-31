package ru.exlmoto.digest.bot.ability.message.impl;

import com.pengrad.telegrambot.model.Message;

//import org.thymeleaf.util.StringUtils;

import org.jsoup.internal.StringUtil;

import ru.exlmoto.digest.bot.ability.message.MessageAbility;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;
import ru.exlmoto.digest.util.text.FilterTextHelper;

public class DigestHashTag extends MessageAbility {
	private final FilterTextHelper filterText;

	public DigestHashTag(FilterTextHelper filterText) {
		this.filterText = filterText;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocalizationHelper locale, Message message) {
		String messageText = isolateMessageText(message.text());
		if (!messageText.isEmpty()) {
			
		}
	}

	protected String isolateMessageText(String message) {
		return filterText.removeHtmlTags(message.replaceAll("#digest|#news", "")).trim();
	}
}
