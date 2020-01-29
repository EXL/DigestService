package ru.exlmoto.digest.bot.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.BotAbility;
import ru.exlmoto.digest.bot.ability.BotAbilityFactory;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.keyboard.BotKeyboard;
import ru.exlmoto.digest.bot.keyboard.BotKeyboardFactory;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.worker.CallbackQueriesWorker;
import ru.exlmoto.digest.util.i18n.LocalizationHelper;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.MessageEntity.Type.hashtag;

@Component
public class BotHandler {
	private final Logger log = LoggerFactory.getLogger(BotHandler.class);

	private final BotConfiguration config;
	private final BotSender sender;
	private final BotHelper helper;
	private final BotTelegram telegram;
	private final BotAbilityFactory abilityFactory;
	private final BotKeyboardFactory keyboardFactory;
	private final CallbackQueriesWorker callbackQueriesWorker;
	private final LocalizationHelper locale;

	public BotHandler(BotConfiguration config,
	                  BotSender sender,
	                  BotHelper helper,
	                  BotTelegram telegram,
	                  BotAbilityFactory abilityFactory,
	                  BotKeyboardFactory keyboardFactory,
	                  CallbackQueriesWorker callbackQueriesWorker,
	                  LocalizationHelper locale) {
		this.config = config;
		this.sender = sender;
		this.helper = helper;
		this.telegram = telegram;
		this.abilityFactory = abilityFactory;
		this.keyboardFactory = keyboardFactory;
		this.callbackQueriesWorker = callbackQueriesWorker;
		this.locale = locale;
	}

	public void onCommand(Message message) {
		final int START = 0;
		for (MessageEntity entity : message.entities()) {
			if (entity.type().equals(bot_command) && entity.offset() == START) {
				Optional<BotAbility> ability =
					abilityFactory.getAbility(message.text().substring(START, entity.length()));
				if (ability.isPresent()) {
					ability.get().process(helper, sender, locale, message);
					return;
				}
			}
		}
		log.info(String.format("Unknown command entity in '%d' chat from '%s' user, message: '%s'.",
			message.chat().id(), helper.getValidUsername(message.from()), message.text()));
	}

	public void onHashTag(Message message) {
		for (MessageEntity entity : message.entities()) {
			if (entity.type().equals(hashtag)) {
				Optional<BotAbility> ability =
					abilityFactory.getAbility(message.text().substring(entity.offset(), entity.length()));
				if (ability.isPresent()) {
					ability.get().process(helper, sender, locale, message);
					return;
				}
			}
		}
	}

	public void onCallbackQuery(CallbackQuery callbackQuery) {
		if (config.isUseStack()) {
			long delay = callbackQueriesWorker.getDelayForChat(callbackQuery.message().chat().id());
			if (delay == 0L) {
				onKeyboard(callbackQuery);
			} else {
				sendCooldownAnswer(callbackQuery.id(), delay);
			}
		} else {
			if (callbackQueriesWorker.getDelay() == 0) {
				callbackQueriesWorker.delayCooldown();
				onKeyboard(callbackQuery);
			} else {
				sendCooldownAnswer(callbackQuery.id(), callbackQueriesWorker.getDelay());
			}
		}
	}

	public void onKeyboard(CallbackQuery callbackQuery) {
		keyboardFactory.getKeyboard(chopCallbackData(callbackQuery.data()))
			.ifPresent(botKeyboard -> botKeyboard.process(helper, sender, locale, callbackQuery));
	}

	public String chopCallbackData(String data) {
		int find = data.indexOf(BotKeyboard.DELIMITER);
		if (find != -1) {
			return data.substring(0, find) + BotKeyboard.DELIMITER;
		}
		return data;
	}

	private void sendCooldownAnswer(String callbackQueryId, long cooldownSec) {
		sender.sendCallbackQueryAnswer(callbackQueryId,
			String.format(locale.i18n("bot.inline.error.cooldown"), cooldownSec));
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
