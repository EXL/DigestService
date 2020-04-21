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

package ru.exlmoto.digest.bot.ability.keyboard;

public enum Keyboard {
	rate,
	chart,
	subscribe,
	digest,
	show,
	greeting;

	private static final String DELIMITER = "_";
	public static final String PAGE = "page" + DELIMITER;

	public String withName() {
		return name() + DELIMITER;
	}

	public static String chopKeyboardNameRight(String key) {
		int find = key.indexOf(DELIMITER);
		return (find != -1) ? key.substring(0, find) + DELIMITER : key;
	}

	public static String chopKeyboardNameLeft(String key) {
		int find = key.indexOf(DELIMITER);
		return (find != -1) ? key.substring(find + 1) : key;
	}

	public static String chopKeyboardNameLast(String key) {
		int find = key.lastIndexOf(DELIMITER);
		return (find != -1) ? key.substring(find + 1) : key;
	}
}
