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
