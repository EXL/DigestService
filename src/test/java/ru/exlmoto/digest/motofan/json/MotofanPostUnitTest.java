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

package ru.exlmoto.digest.motofan.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotofanPostUnitTest {
	private final MotofanPost motofanPostFirst = new MotofanPost();
	private final MotofanPost motofanPostSecond = new MotofanPost();
	private final MotofanPost motofanPostThird = new MotofanPost();

	private final MotofanPostHelper post = new MotofanPostHelper();

	@Test
	public void testMotofanPost() {
		assertFalse(motofanPostFirst.isValid());

		motofanPostSecond.setAuthor("author");
		assertFalse(motofanPostSecond.isValid());

		motofanPostThird.setAuthor("author");
		motofanPostThird.setPost(1L);
		motofanPostThird.setPost_link("link");
		motofanPostThird.setText("text");
		motofanPostThird.setTime("time");
		motofanPostThird.setTimestamp(1L);
		motofanPostThird.setTitle("title");
		motofanPostThird.setTopic_link("link");
		assertFalse(motofanPostThird.isValid());

		motofanPostThird.setTopic(1L);
		assertTrue(motofanPostThird.isValid());

		assertTrue(post.getRandomMotofanPost(1L).isValid());
	}
}
