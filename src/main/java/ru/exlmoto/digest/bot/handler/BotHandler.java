package ru.exlmoto.digest.bot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.ability.BotAbilityFactory;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.util.*;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.MessageEntity.Type.hashtag;

@Component
public class BotHandler {
	private final Logger log = LoggerFactory.getLogger(BotHandler.class);

	private int delay = 0;
	private HashMap<Long, Long> callbackQueriesMap = new HashMap<>();

	private final BotConfiguration config;
	private final BotSender sender;
	private final BotHelper helper;
	private final BotTelegram telegram;
	private final BotAbilityFactory factory;
	private final LocalizationHelper locale;

	public BotHandler(BotConfiguration config,
	                  BotSender sender,
	                  BotHelper helper,
	                  BotTelegram telegram,
	                  BotAbilityFactory factory,
	                  LocalizationHelper locale) {
		this.config = config;
		this.sender = sender;
		this.helper = helper;
		this.telegram = telegram;
		this.factory = factory;
		this.locale = locale;
	}

	@Scheduled(cron = "${cron.bot.callbacks.clear}")
	public void clearCallbackQueriesMap() {
		log.info(String.format("=> Start clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
		callbackQueriesMap.clear();
		log.info(String.format("=> End clear Callback Queries Map, size: '%d'.", callbackQueriesMap.size()));
	}

	public void onCommand(Message message) {
		final int START = 0;
		for (MessageEntity entity : message.entities()) {
			if (entity.type().equals(bot_command) && entity.offset() == START) {
				Optional<BotAbility> ability = factory.getAbility(message.text().substring(START, entity.length()));
				if (ability.isPresent()) {
					ability.ifPresent(commandAbility -> commandAbility.process(helper, sender, locale, message));
					return;
				}
			}
		}
	}

	public void onHashTag(Message message) {
		for (MessageEntity entity : message.entities()) {
			if (entity.type().equals(hashtag)) {
				Optional<BotAbility> ability =
					factory.getAbility(message.text().substring(entity.offset(), entity.length()));
				if (ability.isPresent()) {
					ability.ifPresent(hashTagAbility -> hashTagAbility.process(helper, sender, locale, message));
					return;
				}
			}
		}
	}

	public void onCallbackQuery(CallbackQuery callbackQuery) {
		int cooldown = config.getCooldown();
		if (config.isUseStack()) {
			long chatId = callbackQuery.message().chat().id();
			long currentTime = helper.getCurrentUnixTime();
			if (callbackQueriesMap.containsKey(chatId) || callbackQueriesMap.get(chatId) <= currentTime - cooldown) {
				callbackQueriesMap.put(chatId, currentTime);
				// HandleCallbackQuery
			} else {
				sendCooldownAnswer(callbackQuery.id(),
					cooldown - (currentTime - callbackQueriesMap.get(chatId)));
			}
		} else {
			if (delay == 0) {
				delayCooldown(cooldown);
				// HandleCallbackQuery
			} else {
				sendCooldownAnswer(callbackQuery.id(), delay);
			}
		}
	}

	private void sendCooldownAnswer(String callbackQueryId, long cooldownSec) {
		sender.sendCallbackQueryAnswer(callbackQueryId,
			String.format(locale.i18n("bot.inline.error.cooldown"), cooldownSec));
	}

	private void delayCooldown(int cooldown) {
		delay = cooldown;
		new Thread(() -> {
			try {
				while (delay > 0) {
					// Seconds to milliseconds, 1 second.
					Thread.sleep(1000);
					delay -= 1;
				}
			} catch (InterruptedException ie) {
				log.error("Cannot delay cooldown thread.", ie);
			}
		}).start();
	}

	public void onNewUsers(Message message) {
		if (config.isShowGreetings()) {
			List<User> users = Arrays.asList(message.newChatMembers());
			String usernames;
			if (users.size() == 1) {
				usernames = helper.getValidUsername(users.get(0));
			} else {
				StringJoiner joiner = new StringJoiner(", ");
				users.forEach(user -> joiner.add(helper.getValidUsername(user)));
				usernames = joiner.toString();
			}
			boolean isBotHere = usernames.contains(telegram.getUsername());
			String botAnswer = (isBotHere) ?
				locale.i18n("bot.event.added") :
				locale.i18nRU("bot.event.user.new", usernames);
			sender.replyMessage(message.chat().id(), message.messageId(), botAnswer);
		}
	}

	public void onLeftUser(Message message) {
		if (config.isShowGreetings()) {
			String username = helper.getValidUsername(message.leftChatMember());
			if (!username.equals(telegram.getUsername())) {
				sender.replyMessage(message.chat().id(), message.messageId(),
					locale.i18nRU("bot.event.user.left", username));
			}
		}
	}

	public void onNewPhotos(Message message) {
		sender.replyMessage(message.chat().id(), message.messageId(), locale.i18n("bot.event.photo.change"));
	}
}
