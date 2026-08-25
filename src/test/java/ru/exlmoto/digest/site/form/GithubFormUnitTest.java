/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2026 EXL <exlmotodev@gmail.com>
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

package ru.exlmoto.digest.site.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GithubFormUnitTest {
	@Test
	void acceptsRepositoryUrlsCommentsAndBlankLines() {
		GithubForm form = new GithubForm();
		form.setGithubRepos(
			"\n# Public repositories\n" +
			"https://github.com/EXL/DigestService\n\n" +
			"https://github.com/MotoFanRu/Hitagi/\n"
		);

		assertTrue(form.validate());
		assertNull(form.getValidationError());
	}

	@Test
	void rejectsNonRepositoryValues() {
		GithubForm form = new GithubForm();
		form.setGithubRepos(
			"https://github.com/EXL\n" +
			"http://github.com/EXL/DigestService\n" +
			"random text"
		);

		assertFalse(form.validate());
		assertTrue(form.getValidationError().contains("Line 1:"));
		assertTrue(form.getValidationError().contains("Line 2:"));
		assertTrue(form.getValidationError().contains("Line 3:"));
	}

	@Test
	void acceptsBlankRepositoryList() {
		GithubForm form = new GithubForm();
		form.setGithubRepos("\n # disabled\n");

		assertTrue(form.validate());
	}
}
