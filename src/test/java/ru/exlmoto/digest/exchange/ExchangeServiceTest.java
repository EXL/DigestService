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

package ru.exlmoto.digest.exchange;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.exchange.key.ExchangeKey;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeServiceTest {
	@Autowired
	private ExchangeService service;

	@Test
	public void testUpdateAllRates() {
		service.updateAllRates();
	}

	@Test
	public void testMarkdownReports() {
		assertThat(service.markdownReport(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(service.markdownReport(ExchangeKey.metal_ru.name())).isNotBlank();
	}

	@Test
	public void testJsonReport() {
		assertThat(service.jsonReport()).isNotBlank();
	}

	@Test
	public void testButtonLabels() {
		assertThat(service.buttonLabel(ExchangeKey.bank_ru.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_ua.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_by.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.bank_kz.name())).isNotBlank();
		assertThat(service.buttonLabel(ExchangeKey.metal_ru.name())).isNotBlank();
	}
}
