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

package ru.exlmoto.digest.bot.worker;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FlatWorkerTest {
	@Autowired
	private FlatWorker worker;

	@Test
	public void testGetSubscriberIds() {
		assertThat(worker.getSubscriberIds(null)).isEmpty();
		assertThat(worker.getSubscriberIds("")).isEmpty();
		assertThat(worker.getSubscriberIds("null")).isEmpty();
		assertThat(worker.getSubscriberIds("null null")).isEmpty();
		assertThat(worker.getSubscriberIds("null,null")).isEmpty();

		testIdsString("-10", 1);
		testIdsString("-1", 1);
		testIdsString("-0", 1);
		testIdsString("0", 1);
		testIdsString("1", 1);
		testIdsString("10", 1);
		testIdsString("10,12", 2);
		testIdsString("10,16", 2);
		testIdsString("10, 16", 2);
		testIdsString("10 16", 2);
		testIdsString("10,-324", 2);
		testIdsString("10,null", 1);
		testIdsString("10,null,213", 2);
		testIdsString("10,34234234,213", 3);
	}

	private void testIdsString(String id, int size) {
		List<Long> res = worker.getSubscriberIds(id);
		assertThat(res).isNotEmpty();
		assertThat(res.size()).isEqualTo(size);
		System.out.println(res.toString());
	}
}
