/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.util.filter;

import org.jsoup.Jsoup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class FilterHelper {
	private final Logger log = LoggerFactory.getLogger(FilterHelper.class);

	private final LocaleHelper locale;

	private String ellipsis;

	public FilterHelper(LocaleHelper locale) {
		this.locale = locale;
	}

	@PostConstruct
	private void setUp() {
		ellipsis = locale.i18n("bot.command.show.ellipsis");
	}

	/*
	 * Remove html tags from text.
	 * See: https://stackoverflow.com/questions/14445386/how-to-remove-text-in-brackets-from-the-start-of-a-string
	 */
	public String removeHtmlTags(String html) {
		return Jsoup.parse(html).text();
	}

	public String removeBbCodes(String str) {
		return str
			.replaceAll("\\[.*?]", " ")
			.replaceAll("\\[\\\\", " ")
			.replaceAll("\\[", " ").trim()
			.replaceAll(" +", " ");
	}

	public String checkLink(String url) {
		return url.endsWith("/") ? url : url + "/";
	}

	public String getDateFromTimeStamp(String dateFormat, long timestamp) {
		return DateTimeFormatter.ofPattern(dateFormat)
			.withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(timestamp));
	}

	public String getDateFromTimeStamp(String dateFormat, Locale dateLocale, long timestamp) {
		return DateTimeFormatter.ofPattern(dateFormat)
			.withLocale(dateLocale).withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(timestamp));
	}

	public long getCurrentUnixTime() {
		return System.currentTimeMillis() / 1000L;
	}

	public String strip(String str) {
		return (!StringUtils.isEmpty(str)) ? str.replaceAll("\\s+", " ").trim() : str;
	}

	public String escapeTags(String str) {
		return str.replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	}

	public String removeUserCasts(String str) {
		return str.replaceAll("\\B@", "");
	}

	public String arrangeString(String string, int length) {
		int stringLength = string.length();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length - stringLength; ++i) {
			stringBuilder.append(' ');
		}
		return string + stringBuilder.toString();
	}

	protected String ellipsisString(String text, int length, String ellipsis, int side, boolean arrange) {
		if (text != null && length > 0) {
			int textLength = text.length();
			if (textLength < length) {
				return (arrange) ? arrangeString(text, length) : text;
			}
			if (side < 0) {
				return ellipsis + text.substring(textLength - length + 1);
			} else if (side > 0) {
				return text.substring(0, length - 1) + ellipsis;
			} else {
				int end = textLength - (length / 2);
				return text.substring(0, length / 2) + ellipsis + ((end >= textLength) ? "" : text.substring(end));
			}
		}
		return text;
	}

	public String ellipsisRight(String text, int length) {
		return ellipsisString(text, length, ellipsis, 1, false);
	}

	public String ellipsisRightA(String text, int length) {
		return ellipsisString(text, length, ellipsis, 1, true);
	}

	public String ellipsisLeft(String text, int length) {
		return ellipsisString(text, length, ellipsis, -1, false);
	}

	public String ellipsisLeftA(String text, int length) {
		return ellipsisString(text, length, ellipsis, -1, true);
	}

	public String ellipsisMiddle(String text, int length) {
		return ellipsisString(text, length, ellipsis, 0, false);
	}

	public String activateLink(String link, String text) {
		return activateLink(link, text, null);
	}

	public String activateLink(String link, String text, String title) {
		return StringUtils.isEmptyOrWhitespace(link) ?
			link :
			(title != null) ?
				"<a href=\"" + link + "\" target=\"_blank\" title=\"" + title + "\">" + text + "</a>" :
				"<a href=\"" + link + "\" target=\"_blank\">" + text + "</a>";
	}

	public String getSiteUrlFromLink(String link) {
		try {
			URL url = new URL(link);
			int port = url.getPort();
			return (port == -1) ?
				String.format("%s://%s", url.getProtocol(), url.getHost()) :
				String.format("%s://%s:%d", url.getProtocol(), url.getHost(), port);
		} catch (MalformedURLException mue) {
			log.warn(String.format("Wrong URL link: '%s'.", link), mue);
			return link;
		}
	}
}
