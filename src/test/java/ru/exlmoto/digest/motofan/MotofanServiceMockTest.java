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

package ru.exlmoto.digest.motofan;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.motofan.json.MotofanPostHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@SpringBootTest
class MotofanServiceMockTest {
	@SpyBean
	private MotofanService service;

	private final MotofanPostHelper post = new MotofanPostHelper();

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsNull() {
		List<MotofanPost> firstStep = service.getLastMotofanPosts();
		List<MotofanPost> secondStep = service.getLastMotofanPosts();
		assertNull(firstStep);
		assertNull(secondStep);
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsBugWithWrongArraySize() {
		List<MotofanPost> firstStep = service.getLastMotofanPosts();
		assertThat(firstStep).isNull();

		MotofanPost[] posts = new MotofanPost[2];
		when(service.getMotofanPostObjects()).thenReturn(posts);
		posts[0] = post.getRandomMotofanPost(4000000000000000000L);
		posts[1] = post.getRandomMotofanPost(3000000000000000000L);
		List<MotofanPost> secondStep = service.getLastMotofanPosts();
		assertEquals(2, secondStep.size());
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsFake() {
		MotofanPost[] posts = new MotofanPost[3];
		posts[0] = post.getRandomMotofanPost(2L);
		posts[1] = post.getRandomMotofanPost(1L);
		posts[2] = post.getRandomMotofanPost(0L);
		when(service.getMotofanPostObjects()).thenReturn(posts);
		List<MotofanPost> firstStep = service.getLastMotofanPosts();

		posts[0] = post.getRandomMotofanPost(4L);
		posts[1] = post.getRandomMotofanPost(3L);
		posts[2] = post.getRandomMotofanPost(2L);
		List<MotofanPost> secondStep = service.getLastMotofanPosts();

		assertThat(firstStep).isNull();
		assertThat(secondStep).isNotEmpty();
		assertEquals(2, secondStep.size());
		System.out.println(secondStep);
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsInHtml() {
		MotofanPost[] posts = new MotofanPost[3];
		posts[0] = post.getRandomMotofanPost(2L);
		posts[1] = post.getRandomMotofanPost(1L);
		posts[2] = post.getRandomMotofanPost(0L);
		when(service.getMotofanPostObjects()).thenReturn(posts);
		List<String> firstStep = service.getLastMotofanPostsInHtml();

		posts[0] = post.getRandomMotofanPost(4L);
		posts[1] = post.getRandomMotofanPost(3L);
		posts[2] = post.getRandomMotofanPost(2L);
		List<String> secondStep = service.getLastMotofanPostsInHtml();

		assertThat(firstStep).isEmpty();
		assertThat(secondStep).isNotEmpty();
		assertEquals(2, secondStep.size());
		System.out.println(secondStep);
	}

	@Test
	@DirtiesContext
	public void testGetLastMotofanPostsInHtmlWithNulls() {
		MotofanPost[] posts = new MotofanPost[2];
		posts[0] = post.getRandomMotofanPost(1L);
		posts[1] = post.getRandomMotofanPost(0L);
		when(service.getMotofanPostObjects()).thenReturn(posts);
		service.getLastMotofanPostsInHtml();

		MotofanPost motofanPostFirst = new MotofanPost();
		MotofanPost motofanPostSecond = new MotofanPost();
		motofanPostFirst.setTopic(1L);
		motofanPostSecond.setTopic(1L);
		posts[0] = motofanPostFirst;
		posts[1] = motofanPostSecond;
		assertThrows(NullPointerException.class, () -> service.getLastMotofanPostsInHtml());
	}
}
