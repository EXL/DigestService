/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.exlmoto.digest.bot.generator;

import com.pengrad.telegrambot.model.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

@Component
public class DigestTgHtmlGenerator {
	private final FilterHelper filter;
	private final BotConfiguration config;
	private final BotHelper helper;
	private final LocaleHelper locale;

	@Value("${general.date-format}")
	private String dateFormat;

	public DigestTgHtmlGenerator(FilterHelper filter, BotConfiguration config, BotHelper helper, LocaleHelper locale) {
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
