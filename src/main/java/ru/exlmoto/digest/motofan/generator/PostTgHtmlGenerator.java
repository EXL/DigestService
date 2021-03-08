/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 EXL <exlmotodev@gmail.com>
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

import org.owasp.encoder.Encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.thymeleaf.util.StringUtils;

import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

@Component
public class PostTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(PostTgHtmlGenerator.class);

	private final LocaleHelper locale;
	private final FilterHelper filter;

	@Value("${general.lang}")
	private String lang;

	@Value("${general.date-day-format}")
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
				StringBuilder birthdays = new StringBuilder();
				String birthdaysString = "• " + filter.strip(rawString).replaceAll(" • ", "\n• ");
				BufferedReader reader = new BufferedReader(new StringReader(birthdaysString));
				String line = reader.readLine();
				while (line != null) {
					birthdays.append(filter.replaceLast(line, "(", " ("));
					birthdays.append("\n");
					line = reader.readLine();
				}
				return String.format(locale.i18n("motofan.birthday"),
					filter.getDateFromTimeStamp(dateFormat, Locale.forLanguageTag(lang), filter.getCurrentUnixTime()),
						"<pre>\n" + Encode.forHtml(birthdays.toString().trim()) + "\n</pre>", count);
			} catch (RuntimeException | IOException re_and_ioe) {
				log.error("Cannot parse MotoFan.Ru page.", re_and_ioe);
			}
		}
		return null;
	}
}
