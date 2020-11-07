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

package ru.exlmoto.digest.motofan;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.util.rest.RestHelper;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.file.ResourceHelper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;

@SpringBootTest
class MotofanServiceTest {
	@Value("${motofan.last-post-url}")
	private String lastPostUrl;

	@Autowired
	private MotofanService service;

	@Autowired
	private ResourceHelper resourceHelper;

	@SpyBean
	private RestHelper rest;

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	@Test
	public void testGetLastMotofanPosts() {
		System.out.println("=== START testGetLastMotofanPosts() ===");
		Answer<MotofanPost[]> res = rest.getRestResponse(lastPostUrl, MotofanPost[].class);
		assertThat(res.answer()).isNotEmpty();
		System.out.println(res.answer()[0].getTitle());
		System.out.println("=== END testGetLastMotofanPosts() ===");
	}

	@Test
	public void testGetLastMotofanPostsWrongUrl() {
		assertThat(rest.getRestResponse("fake-url", MotofanPost[].class).answer()).isNull();
		assertThat(rest.getRestResponse("https://exlmoto.ru", MotofanPost[].class).answer()).isNull();
		assertThat(rest.getRestResponse("https://exlmotor.ru", MotofanPost[].class).answer()).isNull();
	}

	@Test
	public void testIncorrectJson() {
		System.out.println("=== START testIncorrectJson() ===");
		when(rest.getRestTemplate()).thenReturn(restTemplate);
		MotofanPost[] posts = fakeJsonRestTemplateResult("classpath:motofan/posts.json");
		assertThat(posts).isNotEmpty();
		System.out.println(posts[0].getTitle());
		assertThat(fakeJsonRestTemplateResult("classpath:motofan/posts-null.json")).isNull();
		assertEquals(8, fakeJsonRestTemplateResult("classpath:motofan/posts-incorrect.json").length);
		assertThat(fakeJsonRestTemplateResult("classpath:motofan/posts-empty.json")).isNull();
		assertThat(fakeJsonRestTemplateResult("classpath:motofan/posts-another.json")).isNull();
		assertThat(fakeJsonRestTemplateResult("classpath:motofan/posts-wrong.json")).isNull();
		assertThat(fakeJsonRestTemplateResult("classpath:motofan/posts-chunk.json")).isNull();
		System.out.println("=== END testIncorrectJson() ===");
	}

	@Test
	public void testGetMotofanBirthdays() {
		System.out.println("=== START testGetMotofanBirthdays() ===");
		String res = service.getMotofanBirthdays();
		assertThat(res).isNotBlank();
		System.out.println(res);
		System.out.println("=== END testGetMotofanBirthdays() ===");
	}

	@Test
	public void testIncorrectHtml() {
		System.out.println("=== START testIncorrectHtml() ===");
		when(rest.getRestTemplate()).thenReturn(restTemplate);
		String res = fakeHtmlRestTemplateResult("classpath:motofan/motofanNull.html", "Windows-1251");
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofanEmpty.html", "Windows-1251");
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofanOther.html", "Windows-1251");
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofanWrong.html", StandardCharsets.UTF_8.name());
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofanWrong.html", "Windows-1251");
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofan.html", StandardCharsets.UTF_8.name());
		System.out.println(res);
		System.out.println("---");
		res = fakeHtmlRestTemplateResult("classpath:motofan/motofan.html", "Windows-1251");
		System.out.println(res);
		System.out.println("=== END testIncorrectHtml() ===");
	}

	private MotofanPost[] fakeJsonRestTemplateResult(String filename) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(manyTimes(), MockRestRequestMatchers.anything()).andRespond(
			MockRestResponseCreators.withSuccess(resourceHelper.readFileToString(filename), MediaType.APPLICATION_JSON)
		);
		return service.getMotofanPostObjects();
	}

	private String fakeHtmlRestTemplateResult(String filename, String charset) {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer.expect(manyTimes(), MockRestRequestMatchers.anything()).andRespond(
			MockRestResponseCreators.withSuccess(resourceHelper.readFileToString(filename, Charset.forName(charset)),
				MediaType.valueOf("text/html; charset=utf-8"))
		);
		return service.getMotofanBirthdays();
	}
}
