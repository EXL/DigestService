/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 EXL <exlmotodev@gmail.com>
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ResourceHelperTest {
	@Value("classpath:chart/charts.yaml")
	private Resource resourceOk;

	@Value("classpath:unknown-file.ext")
	private Resource resourceFail;

	@Autowired
	private ResourceHelper resourceHelper;

	@Test
	public void testReadFileToString() {
		String res = resourceHelper.readFileToString("classpath:chart/charts.yaml");
		assertThat(res).isNotEmpty();
		System.out.println(res.substring(0, 40));

		assertThrows(UncheckedIOException.class,
			() -> resourceHelper.readFileToString("classpath:unknown-file.ext"));
	}

	@Test
	public void testAsString() {
		String res = resourceHelper.asString(resourceOk);
		assertThat(res).isNotEmpty();
		System.out.println(res.substring(0, 40));

		assertThrows(UncheckedIOException.class, () -> resourceHelper.asString(resourceFail));
	}

	@Test
	public void testAsByteArray() {
		byte[] byteArray = resourceHelper.asByteArray(resourceOk);
		assertEquals(String.format("%02X%02X%02X", byteArray[0], byteArray[1], byteArray[2]), "6D6D62"); // JPEG header.

		assertThrows(UncheckedIOException.class, () -> resourceHelper.asByteArray(resourceFail));
	}

	@Test
	public void testGetResourceFilePath() {
		assertNull(resourceHelper.getResourceFilePath(null));
		assertNull(resourceHelper.getResourceFilePath(""));
		assertNull(resourceHelper.getResourceFilePath("classpath:"));
		assertNull(resourceHelper.getResourceFilePath("flat/unknown.file"));
		assertNull(resourceHelper.getResourceFilePath("classpath:flat/unknown.file"));

		checkFilePath(resourceHelper.getResourceFilePath("classpath:flat/cian.xlsx"));
		checkFilePath(resourceHelper.getResourceFilePath("flat/cian.xlsx"));
	}

	private void checkFilePath(String path) {
		assertThat(path).isNotEmpty();
		System.out.println(path);
	}
}
