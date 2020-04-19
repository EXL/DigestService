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

package ru.exlmoto.digest.bot.worker;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CallbackQueriesWorkerTest {
	@Autowired
	private CallbackQueriesWorker worker;

	@Autowired
	private BotConfiguration config;

	@Test
	public void testClearCallbackQueriesMap() {
		worker.clearCallbackQueriesMap();
	}

	@Test
	public void testGetDelayForChat() throws InterruptedException {
		assertEquals(0L, worker.getDelayForChat(0L));
		assertEquals(0L, worker.getDelayForChat(1L));
		assertEquals(config.getCooldown(), worker.getDelayForChat(0L));
		assertEquals(config.getCooldown(), worker.getDelayForChat(1L));
		Thread.sleep((config.getCooldown() + 1) * 1000);
		assertEquals(0L, worker.getDelayForChat(0L));
		assertEquals(0L, worker.getDelayForChat(1L));
	}

	@Test
	public void testDelayCooldown() throws InterruptedException {
		assertEquals(0, worker.getDelay());
		worker.delayCooldown();
		assertEquals(config.getCooldown(), worker.getDelay());
		Thread.sleep((config.getCooldown() + 1) * 1000);
		assertEquals(0, worker.getDelay());
	}

	@Test
	public void testDelayCooldownOnConcurrency() throws InterruptedException {
		assertEquals(0, worker.getDelay());

		int delay = config.getCooldown() * 2 * 1000;

		new Thread(() -> {
			int cooldown = delay;
			while (cooldown > 0) {
				System.out.println(worker.getDelay());
				cooldown -= 1;
				try {
					Thread.sleep(500);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					ie.printStackTrace();
				}
			}
		}).start();

		new Thread(() -> worker.delayCooldown()).start();
		Thread.sleep(2000);
		new Thread(() -> worker.delayCooldown()).start();
		Thread.sleep(2000);
		new Thread(() -> worker.delayCooldown()).start();
		Thread.sleep(5500);
		new Thread(() -> worker.delayCooldown()).start();

		Thread.sleep(delay);

		assertEquals(0, worker.getDelay());
	}
}
