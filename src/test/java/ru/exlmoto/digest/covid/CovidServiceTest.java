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

package ru.exlmoto.digest.covid;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Covid;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CovidServiceTest {
	@Autowired
	private CovidService service;

	@Test
	public void testJsonReport() {
		assertTrue(service.jsonRuReport().startsWith("{"));
		assertTrue(service.jsonUaReport().startsWith("{"));
	}

	@Test
	public void testTgHtmlReport() {
		assertTrue(service.tgHtmlRuReport().startsWith("<"));
		assertTrue(service.tgHtmlUaReport().startsWith("<"));
	}

	@Test
	public void testTgHtmlImageRenderedHtmlReport() {
		assertTrue(service.tgHtmlImageRenderedHtmlReport(service.tgHtmlRuReport()).startsWith("<"));
		assertTrue(service.tgHtmlImageRenderedHtmlReport(service.tgHtmlUaReport()).startsWith("<"));
	}

	@Test
	public void testTgHtmlImageRenderedHtmlReportTitle() {
		assertThat(service.tgHtmlImageRenderedHtmlReportTitle(Covid.ru)).isNotBlank();
	}
}
