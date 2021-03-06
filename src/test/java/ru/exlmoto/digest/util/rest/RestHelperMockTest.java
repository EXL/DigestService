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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.util.Answer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;

@SpringBootTest
class RestHelperMockTest {
	@SpyBean
	private RestHelper rest;

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	@BeforeEach
	public void setUpTests() {
		when(rest.getRestTemplate()).thenReturn(restTemplate);
	}

	@Test
	public void testRestServiceEmptyAnswer() {
		System.out.println("=== START testRestServiceEmptyAnswer() ===");
		Answer<String> res = getContentFromMockServer("");
		assertFalse(res.ok());
		assertNull(res.answer());
		String error = res.error();
		assertThat(error).isNotEmpty();
		System.out.println("Error: " + error);
		System.out.println("=== END testRestServiceEmptyAnswer() ===");
	}

	@Test
	public void testRestServiceNonEmptyAnswer() {
		System.out.println("=== START testRestServiceNonEmptyAnswer() ===");
		Answer<String> res = getContentFromMockServer("Test answer");
		assertTrue(res.ok());
		String answer = res.answer();
		assertThat(answer).isNotEmpty();
		System.out.println("Answer: " + answer);
		System.out.println("=== END testRestServiceNonEmptyAnswer() ===");
	}

	private Answer<String> getContentFromMockServer(String answer) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(manyTimes(), MockRestRequestMatchers.anything())
			.andRespond(MockRestResponseCreators.withSuccess(answer, MediaType.APPLICATION_JSON));
		return rest.getRestResponse("fake-url");
	}
}
