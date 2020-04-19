/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL
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

package ru.exlmoto.digest.exchange.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import ru.exlmoto.digest.service.DatabaseService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@SpringBootTest
class RateJsonGeneratorTest {
	@Autowired
	private RateJsonGenerator generator;

	@SpyBean
	private DatabaseService service;

	@Test
	public void testGetJsonReport() {
		when(service.getBankRu()).thenReturn(Optional.ofNullable(null));
		String report = generator.getJsonReport();
		assertThat(report).isNotBlank();
		System.out.println(report);
	}

	@Test
	public void testGetJsonReportDatabaseError() {
		when(service.getBankRu()).thenThrow(new InvalidDataAccessResourceUsageException("Test exception."));
		String report = generator.getJsonReport();
		assertThat(report).isNotBlank();
		System.out.println(report);
	}
}
