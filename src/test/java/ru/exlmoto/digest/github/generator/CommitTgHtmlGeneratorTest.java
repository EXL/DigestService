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

import org.junit.jupiter.api.Test;

import ru.exlmoto.digest.github.json.GithubCommit;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommitTgHtmlGeneratorTest {
	@Test
	void formatsCommitDateAndEscapesHtml() throws Exception {
		CommitTgHtmlGenerator generator = new CommitTgHtmlGenerator(testLocaleHelper());
		setDateFormat(generator, "yyyy-MM-dd");

		GithubCommit commit = new GithubCommit(
			"1234567890abcdef",
			"https://github.com/EXL/DigestService/commit/1234567",
			new GithubCommit.CommitDetails(
				new GithubCommit.GithubUser("author", "Author", "https://github.com/author"),
				"Fix <bug>",
				"https://api.github.com/commits/1234567",
				new GithubCommit.CommitDate("2026-08-12T08:39:35Z")
			),
			new GithubCommit.GithubUser("author", "Author", "https://github.com/author")
		);

		String result = generator.generateGithubCommitHtmlReport("EXL/DigestService", commit);

		assertTrue(result.contains("2026-08-12"));
		assertTrue(result.contains("<pre>Fix &lt;bug&gt;</pre>"));
		assertTrue(result.contains("1234567"));
	}

	@Test
	void returnsShortSha() {
		assertEquals("1234567", CommitTgHtmlGenerator.getShortSha("1234567890abcdef"));
	}

	private static LocaleHelper testLocaleHelper() {
		return new LocaleHelper(null) {
			@Override
			public String i18n(String key) {
				return switch (key) {
					case "github.new.commit" -> "New GitHub Commit!";
					case "github.datetime" -> "Date/Time:";
					case "github.author" -> "Author:";
					case "github.project" -> "Project:";
					default -> key;
				};
			}
		};
	}

	private static void setDateFormat(CommitTgHtmlGenerator generator, String dateFormat)
			throws ReflectiveOperationException {
		Field field = CommitTgHtmlGenerator.class.getDeclaredField("dateTimeFormat");
		field.setAccessible(true);
		field.set(generator, dateFormat);
	}
}
