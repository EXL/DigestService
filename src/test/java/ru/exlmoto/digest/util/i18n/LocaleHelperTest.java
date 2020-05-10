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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LocaleHelperTest {
	@Autowired
	private LocaleHelper locale;

	@Test
	public void testLocalizationHelper() {
		String res = locale.i18n("exchange.error.report");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		res = locale.i18n("exchange.error.value") + " " + locale.i18n("exchange.change.up");
		assertThat(res).isInstanceOf(String.class);
		System.out.println(res);

		assertEquals("???", locale.i18n("unknown.key"));
	}

	@Test
	public void testUsernameAndRandomLocalizations() {
		String stringWithUsername = locale.i18nU("bot.error.access", "Fake username");
		assertThat(stringWithUsername).isInstanceOf(String.class).isNotEmpty();
		System.out.println(stringWithUsername);

		String randomString = locale.i18nR("bot.event.user.new");
		assertThat(randomString).isInstanceOf(String.class).isNotEmpty();
		System.out.println(randomString);

		String randomStringWithUsername = locale.i18nRU("bot.event.user.new", "Fake username");
		assertThat(randomStringWithUsername).isInstanceOf(String.class).isNotEmpty();
		System.out.println(randomStringWithUsername);
	}

	@Test
	public void testLocalizationStringWithLocale() {
		String stringDefault = locale.i18nW("motofan.title", Locale.getDefault());
		System.out.println(stringDefault);
		assertThat(stringDefault).isInstanceOf(String.class).isNotEmpty();

		String stringEn = locale.i18nW("motofan.title", Locale.forLanguageTag("en"));
		System.out.println(stringEn);
		assertThat(stringEn).isInstanceOf(String.class).isNotEmpty();

		String stringRu = locale.i18nW("motofan.title", Locale.forLanguageTag("ru"));
		System.out.println(stringRu);
		assertThat(stringRu).isInstanceOf(String.class).isNotEmpty();

		String stringNull = locale.i18nW("motofan.title", null);
		System.out.println(stringNull);
		assertThat(stringNull).isInstanceOf(String.class).isNotEmpty();

		assertEquals("???", locale.i18nW("unknown.key", Locale.forLanguageTag("en")));
	}
}
