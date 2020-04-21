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

package ru.exlmoto.digest.util.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class LocaleHelper {
	private final Logger log = LoggerFactory.getLogger(LocaleHelper.class);

	@Value("${general.lang}")
	private String lang;

	@Value("${general.username-tag}")
	private String usernameTag;

	private final MessageSource messageSource;

	public LocaleHelper(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String i18nW(String key, Locale locale) {
		try {
			return messageSource.getMessage(key, null, locale);
		} catch (NoSuchMessageException nsme) {
			log.error(String.format("Message with key '%s' for locale '%s' is missing.", key, locale), nsme);
			return "???";
		}
	}

	public String i18n(String key) {
		return i18nW(key, Locale.forLanguageTag(lang));
	}

	public String i18nU(String key, String username) {
		return i18n(key).replaceAll(usernameTag, username);
	}

	public String i18nR(String key) {
		String[] strings = i18n(key).split("\n");
		return strings[ThreadLocalRandom.current().nextInt(0, strings.length)];
	}

	public String i18nRU(String key, String username) {
		return i18nR(key).replaceAll(usernameTag, username);
	}
}
