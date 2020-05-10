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

package ru.exlmoto.digest.exchange.manager;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.exchange.configuration.ExchangeConfiguration;
import ru.exlmoto.digest.service.DatabaseService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@SpringBootTest
class ExchangeManagerTest {
	@Autowired
	private ExchangeManager manager;

	@Autowired
	private ExchangeConfiguration config;

	@MockBean
	private DatabaseService service;

	@Test
	public void testRateException() {
		when(service.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		manager.commitBankRu(config.getBankRu(), config.getBankRuMirror());

		when(service.getBankUa()).thenReturn(null);
		assertNull(service.getBankUa());

		when(service.getMetalRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		assertThrows(InvalidDataAccessResourceUsageException.class, () -> service.getMetalRu());
	}

	@Test
	public void testRateMirrors() {
		System.out.println("=== START testRateMirrors() ===");
		manager.commitBankRu(null, config.getBankRuMirror());
		manager.commitBankUa(null, config.getBankUaMirror());
		manager.commitMetalRu(null, config.getMetalRuMirror());
		System.out.println("=== END testRateMirrors() ===");
	}
}
