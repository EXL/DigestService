/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.motofan.generator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.motofan.json.MotofanPost;
import ru.exlmoto.digest.motofan.json.MotofanPostHelper;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class PostTgHtmlGeneratorTest {
	@Autowired
	private PostTgHtmlGenerator htmlGenerator;

	private final MotofanPostHelper post = new MotofanPostHelper();

	@Test
	public void testGeneratorTgHtmlOnNulls() {
		assertThrows(NullPointerException.class, () -> htmlGenerator.generateMotofanPostHtmlReport(null));
		assertThrows(NullPointerException.class, () -> htmlGenerator.filterMotofanPost(null));
	}

	@Test
	public void testGenerateHtmlReport() {
		System.out.println("=== START testGenerateHtmlReport() ===");
		generateHtmlReports(1L);
		generateHtmlReports(2L);
		generateHtmlReports(3L);
		System.out.println("=== END testGenerateHtmlReport() ===");
	}

	@Test
	public void testFilterMotofanPost() {
		assertEquals("", htmlGenerator.filterMotofanPost(""));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test [b]text[/b] test"));
		assertEquals("test text test bl", htmlGenerator.filterMotofanPost("test [b]text[/b] test [bl"));
		assertEquals("test text test bl", htmlGenerator.filterMotofanPost("test [b]text[/b] test [\\bl"));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test <b>text</b> test"));
		assertEquals("test text test", htmlGenerator.filterMotofanPost("test <b id=\"a\">text</b> test"));
	}

	@Test
	public void testGenerateMotofanBirthdaysReport() {
		assertNull(htmlGenerator.generateMotofanBirthdaysReport(null));
		assertNull(htmlGenerator.generateMotofanBirthdaysReport(""));
		assertNull(htmlGenerator.generateMotofanBirthdaysReport(" "));
		assertNull(htmlGenerator.generateMotofanBirthdaysReport("test"));
		assertNull(htmlGenerator.generateMotofanBirthdaysReport("<html>test</html>"));
	}

	@Test
	public void testGenerateMotofanPostWithHtmlTags() {
		MotofanPost motofanPost = post.getRandomMotofanPost(42L);

		motofanPost.setAuthor(">>author<<>>");
		motofanPost.setTitle(">>title<<>>");
		motofanPost.setText(">>text<<>>");

		assertEquals(">>author<<>>", motofanPost.getAuthor());
		assertEquals(">>title<<>>", motofanPost.getTitle());
		assertEquals(">>text<<>>", motofanPost.getText());

		motofanPost.setAuthor(htmlGenerator.filterMotofanPost(">>author<<>>"));
		motofanPost.setTitle(htmlGenerator.filterMotofanPost(">>title<<>>"));
		motofanPost.setText(htmlGenerator.filterMotofanPost(">>text<<>>"));

		assertEquals("&gt;&gt;author&lt;&lt;&gt;&gt;", motofanPost.getAuthor());
		assertEquals("&gt;&gt;title&lt;&lt;&gt;&gt;", motofanPost.getTitle());
		assertEquals("&gt;&gt;text&lt;&lt;&gt;&gt;", motofanPost.getText());
	}

	private void generateHtmlReports(long timestamp) {
		String result = htmlGenerator.generateMotofanPostHtmlReport(post.getRandomMotofanPost(timestamp));
		assertThat(result).isNotEmpty();
		System.out.println(result + "\n");
	}
}
