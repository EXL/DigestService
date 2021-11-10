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

package ru.exlmoto.digest.util.file;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.util.Answer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = { "image.download-file=true", "image.use-image-io-read=true" })
class ImageHelperTest {
	@Autowired
	private ImageHelper imageHelper;

	@Test
	public void testGetImageByLink() {
		System.out.println("=== START testGetImageByLink() ===");
		// Wrong link.
		Answer<String> res = imageHelper.getImageByLink("https://exlmotor.ru");
		assertFalse(res.ok());
		System.out.println("Get: " + res.error());

		// Wrong content.
		res = imageHelper.getImageByLink("https://exlmoto.ru");
		assertFalse(res.ok());
		System.out.println("Get: " + res.error());

		// Good image.
		res = imageHelper.getImageByLink("https://www.apple.com/apple-touch-icon.png");
		assertTrue(res.ok());
		System.out.println("Get: " + res.answer());

		// Another good image without url-extension.
		res = imageHelper.getImageByLink("https://forum.motofan.ru/index.php?act=Attach&type=post&id=277255");
		assertTrue(res.ok());
		System.out.println("Get: " + res.answer());
		System.out.println("=== END testGetImageByLink() ===");
	}
}
