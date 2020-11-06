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

package ru.exlmoto.digest.motofan.generator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.filter.FilterHelper;

@Component
public class PostTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(PostTgHtmlGenerator.class);

	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${general.date-short-format}")
	private String dateFormat;

	private final int MOTOFAN_BIRTHDAY_TABLE_INDEX = 1;

	public PostTgHtmlGenerator(LocaleHelper locale, FilterHelper filter) {
		this.locale = locale;
		this.filter = filter;
	}

	public String generateMotofanPostHtmlReport(MotofanPost post) {
		return
			locale.i18n("motofan.title") + "\n\n" +
			"<b>" + post.getAuthor() + "</b> " + locale.i18n("motofan.wrote") +
			" (<i>" + post.getTime() + "</i>):\n" + "<i>" + filterMotofanPost(post.getText()) + "</i>\n\n" +
			locale.i18n("motofan.read") + " <a href=\"" + post.getPost_link() + "\">" + post.getTitle() + "</a>";
	}

	public String filterMotofanPost(String text) {
		return filter.removeBbCodes(filter.removeHtmlTags(text));
	}

	public String generateMotofanBirthdaysReport(String rawHtml) {
		if (!StringUtils.isEmptyOrWhitespace(rawHtml)) {
			try {
				Element table = Jsoup.parse(rawHtml).getElementsByClass("formsubtitle")
					.get(MOTOFAN_BIRTHDAY_TABLE_INDEX).parent().parent();
				Element cell = table.getElementsByClass("row2").get(MOTOFAN_BIRTHDAY_TABLE_INDEX);
				int count = Integer.parseInt(cell.getElementsByTag("b").first().text());
				String cellString = cell.toString();
				String rawString = filter.removeHtmlTags(cellString.substring(cellString.indexOf("<a href")));
				String birthdays = "• " + filter.strip(rawString.replaceAll("\\(", " (")).replaceAll("•", "\n•");
				return String.format(locale.i18n("motofan.birthday"),
					filter.getDateFromTimeStamp(dateFormat, filter.getCurrentUnixTime()),
						"<pre>\n" + birthdays + "\n</pre>", count);
			} catch (RuntimeException re) {
				log.error("Cannot parse MotoFan.Ru page.", re);
			}
		}
		return null;
	}
}
