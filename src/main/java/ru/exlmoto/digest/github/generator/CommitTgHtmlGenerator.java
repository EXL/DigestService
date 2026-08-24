/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.github.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.owasp.encoder.Encode;

import ru.exlmoto.digest.github.json.GithubCommit;
import ru.exlmoto.digest.util.i18n.LocaleHelper;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.time.Instant;

@Component
public class CommitTgHtmlGenerator {
	private final Logger log = LoggerFactory.getLogger(CommitTgHtmlGenerator.class);

	private final LocaleHelper locale;

	@Value("${general.date-short-format}")
	private String dateShortFormat;

	public CommitTgHtmlGenerator(LocaleHelper locale) {
		this.locale = locale;
	}

	public String generateGithubCommitHtmlReport(String repoName, GithubCommit commit) {
		try {
			// String shaLong = commit.sha();
			String shaShort = commit.sha().substring(0, 7);
			String commitUrl = commit.htmlUrl();
			String commitDate = FilterHelper.getDateFromTimeStamp(
				dateShortFormat,
				Instant.parse(commit.commit().committer().date()).getEpochSecond()
			);
			String repoUrl = "https://github.com/" + repoName;

			// Commit author details.
			String authorName = commit.commit().author().name();
			String authorLink = (commit.author() != null) ? commit.author().htmlUrl() : null;

			String authorFormatted = (authorLink != null)
					? String.format("<a href=\"%s\">%s</a>", authorLink, Encode.forHtml(authorName))
					: Encode.forHtml(authorName);

			String commitMessage = Encode.forHtml(commit.commit().message().trim());

			return String.format(
				"<b>%s</b>\n" +
				"<i>%s %s</i>\n\n" +
				"%s <b>%s</b> @ <a href=\"%s\">%s</a>\n\n" +
				"<code>%s</code>\n\n" +
				"%s <b><a href=\"%s\">%s</a></b> %s",
				locale.i18n("github.new.commit"),
				locale.i18n("github.hash"), commitDate,
				locale.i18n("github.author"), authorFormatted, commitUrl, shaShort,
				commitMessage,
				locale.i18n("github.link.icon"), repoUrl, repoUrl, locale.i18n("github.link_back.icon")
			);
		} catch (Exception e) {
			log.error(String.format("==> Cannot get repo/commit information for '%s'", repoName), e);
		}
		return "";
	}
}
