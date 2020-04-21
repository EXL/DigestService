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

package ru.exlmoto.digest.util;

public final class Answer<T> {
	private final String error;
	private final T answer;

	private Answer(String error, T answer) {
		this.error = error;
		this.answer = answer;
	}

	public static <T> Answer<T> Error(String error) {
		return new Answer<>(error, null);
	}

	public static <T> Answer<T> Ok(T answer) {
		return new Answer<>("", answer);
	}

	public boolean ok() {
		return error.isEmpty();
	}

	public String error() {
		return error;
	}

	public T answer() {
		return answer;
	}

	@Override
	public String toString() {
		return
			"Answer{error=" + error +
			", answer=" + answer +
			"}";
	}
}
