package ru.exlmoto.digest.bot.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.handler.BotHandler;

import java.util.Map;

@Component
public class CallbackQueriesWorker {
	private final Logger log = LoggerFactory.getLogger(CallbackQueriesWorker.class);

	private BotHandler handler;

	@Autowired
	public void setBotHandler(@Lazy BotHandler handler) {
		this.handler = handler;
	}

	@Scheduled(cron = "${cron.bot.callbacks.clear}")
	public void clearCallbackQueriesMap() {
		Map<Long, Long> callbackQueriesMap = handler.getCallbackQueriesMap();
		log.info(String.format("=> Start clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
		callbackQueriesMap.clear();
		log.info(String.format("=> End clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
	}
}
