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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.util.filter.FilterHelper;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallbackQueriesWorker {
	private final Logger log = LoggerFactory.getLogger(CallbackQueriesWorker.class);

	private final Map<Long, Long> callbackQueriesMap = new HashMap<>();
	private int delay = 0;

	private final BotConfiguration config;
	private final FilterHelper filter;

	public CallbackQueriesWorker(BotConfiguration config, FilterHelper filter) {
		this.config = config;
		this.filter = filter;
	}

	@Scheduled(cron = "${cron.bot.callbacks.clear}")
	public void clearCallbackQueriesMap() {
		log.info(String.format("=> Start clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
		callbackQueriesMap.clear();
		log.info(String.format("=> End clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
	}

	public long getDelayForChat(long chatId) {
		int cooldown = config.getCooldown();
		long currentTime = filter.getCurrentUnixTime();
		if (!callbackQueriesMap.containsKey(chatId) || callbackQueriesMap.get(chatId) <= currentTime - cooldown) {
			callbackQueriesMap.put(chatId, currentTime);
			return 0L;
		}
		return cooldown - (currentTime - callbackQueriesMap.get(chatId));
	}

	public void delayCooldown() {
		delay = config.getCooldown();
		new Thread(() -> {
			synchronized (this) {
				while (delay > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						log.error("Cannot delay cooldown thread.", ie);
						delay = 0;
						Thread.currentThread().interrupt();
						break;
					}
					delay -= 1;
				}
			}
		}).start();
	}

	public int getDelay() {
		return delay;
	}

	public int getCallbackQueriesMapSize() {
		return callbackQueriesMap.size();
	}
}
