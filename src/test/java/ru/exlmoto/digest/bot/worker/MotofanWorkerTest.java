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

package ru.exlmoto.digest.bot.worker;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import ru.exlmoto.digest.util.rest.RestHelper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static ru.exlmoto.digest.util.Answer.Error;

@SpringBootTest
class MotofanWorkerTest {
	@Autowired
	private MotofanWorker worker;

	@SpyBean
	private RestHelper rest;

	@Test
	public void testGenerateGoodMorningBirthdayReport() {
		System.out.println("=== START testGenerateGoodMorningBirthdayReport() ===");
		System.out.println(worker.generateGoodMorningBirthdayReport());
		System.out.println("---");
		when(rest.getRestResponse(any())).thenReturn(Error("Some Test Error!"));
		System.out.println(worker.generateGoodMorningBirthdayReport());
		System.out.println("=== END testGenerateGoodMorningBirthdayReport() ===");
	}
}
