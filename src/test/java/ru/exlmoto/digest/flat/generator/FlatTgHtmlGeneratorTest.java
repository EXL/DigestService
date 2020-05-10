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

package ru.exlmoto.digest.flat.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.exlmoto.digest.flat.parser.impl.FlatCianParser;
import ru.exlmoto.digest.util.file.ResourceHelper;
import ru.exlmoto.digest.util.rest.RestHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Ok;

@SpringBootTest
class FlatTgHtmlGeneratorTest {
	@Autowired
	private FlatTgHtmlGenerator generator;

	@MockBean
	private RestHelper rest;

	@Autowired
	private ResourceHelper helper;

	@Autowired
	private FlatCianParser parser;

	@Test
	public void testGetTgHtmlReportCian() {
		System.out.println("=== START testGetTgHtmlReportCian() ===");
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportCian() ===");
	}

	@Test
	public void testGetTgHtmlReportCianOnError() {
		System.out.println("=== START testGetTgHtmlReportCianOnError() ===");
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-broken.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportCianOnError() ===");
	}

	@Test
	public void testGetTgHtmlReportCianOnEmpty() {
		System.out.println("=== START testGetTgHtmlReportCianOnEmpty() ===");
		parser.setDeleteXslxFile(false);

		when(rest.getRestFile(anyString()))
			.thenReturn(Ok(helper.getResourceFilePath("classpath:flat/cian-empty.xlsx")));

		checkReport(generator.getTgHtmlReportCian("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportCianOnEmpty() ===");
	}

	@Test
	public void testGetTgHtmlReportN1() {
		System.out.println("=== START testGetTgHtmlReportN1() ===");
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportN1() ===");
	}

	@Test
	public void testGetTgHtmlReportN1OnError() {
		System.out.println("=== START testGetTgHtmlReportN1OnError() ===");
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-broken.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportN1OnError() ===");
	}

	@Test
	public void testGetTgHtmlReportN1OnEmpty() {
		System.out.println("=== START testGetTgHtmlReportN1OnEmpty() ===");
		when(rest.getRestResponse(anyString()))
			.thenReturn(Ok(helper.readFileToString("classpath:flat/n1-empty.json")));

		checkReport(generator.getTgHtmlReportN1("https://example.com", "https://example.com", 25));
		System.out.println("=== END testGetTgHtmlReportN1OnEmpty() ===");
	}

	private void checkReport(String report) {
		assertThat(report).isNotEmpty();
		System.out.println(report);
	}
}
