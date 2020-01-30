package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.util.BotHelper;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallbackQueriesWorker {
	private final Logger log = LoggerFactory.getLogger(CallbackQueriesWorker.class);

	private final Map<Long, Long> callbackQueriesMap = new HashMap<>();
	private int delay = 0;

	private final BotConfiguration config;
	private final BotHelper helper;

	public CallbackQueriesWorker(BotConfiguration config, BotHelper helper) {
		this.config = config;
		this.helper = helper;
	}

	@Scheduled(cron = "${cron.bot.callbacks.clear}")
	public void clearCallbackQueriesMap() {
		log.info(String.format("=> Start clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
		callbackQueriesMap.clear();
		log.info(String.format("=> End clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
	}

	public long getDelayForChat(long chatId) {
		int cooldown = config.getCooldown();
		long currentTime = helper.getCurrentUnixTime();
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
}
