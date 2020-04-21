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

package ru.exlmoto.digest.util.rest;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RestHelperTest {
	@Autowired
	private RestHelper rest;

	@Test
	public void testGetRestContent() {
		Answer<String> res = rest.getRestResponse("https://exlmoto.ru");
		assertTrue(res.ok());
		assertThat(res.answer()).isNotEmpty();
		assertThat(res.error()).isEmpty();

		res = rest.getRestResponse("https://exlmotor.ru");
		assertFalse(res.ok());
		assertThat(res.error()).isNotEmpty();
		System.out.println(res.error());
	}

	@Test
	public void testDropLargeRestContent() {
		Answer<String> res = rest.getRestResponse("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
		assertFalse(res.ok());
		assertThat(res.error()).isNotEmpty();
		System.out.println(res.error());
	}

	@Test
	public void testGetRestFile() {
		System.out.println(rest.getRestFile("https://api.z-lab.me/charts/mmvb.png").answer());
		System.out.println(rest.getRestFile("https://www.linux.org.ru/tracker/").answer());
		System.out.println(rest.getRestFile("https://exlmoto.ru/").answer());
		assertNull(rest.getRestFile("https://exlmoto.ru/404").answer());
	}

	@Test
	public void testRestFileManagerLargeFileDrop() {
		Answer<String> res = rest.getRestFile("https://mirror.yandex.ru/astra/current/orel/iso/orel-current.iso");
		assertNull(res.answer());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println(error);
	}
}
