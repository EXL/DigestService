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
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.telegram.BotTelegram;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.bot.worker.CallbackQueriesWorker;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

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
	private final BotTelegram telegram;
	private final BotHelper helper;
	private final LocaleHelper locale;
	private final BotSender sender;
	private final BotAbilityFactory abilityFactory;
	private final CallbackQueriesWorker callbackQueriesWorker;
	private final DatabaseService service;

	public BotHandler(BotConfiguration config,
	                  BotTelegram telegram,
	                  BotHelper helper,
	                  LocaleHelper locale,
	                  BotSender sender,
	                  BotAbilityFactory abilityFactory,
	                  CallbackQueriesWorker callbackQueriesWorker,
	                  DatabaseService service) {
		this.config = config;
		this.sender = sender;
		this.helper = helper;
		this.telegram = telegram;
		this.abilityFactory = abilityFactory;
		this.callbackQueriesWorker = callbackQueriesWorker;
		this.locale = locale;
		this.service = service;
	}

	public void onCommand(Message message) {
		final int START = 0;
		MessageEntity[] messageEntities = message.entities();
		for (MessageEntity entity : messageEntities) {
			if (entity.type().equals(bot_command) && entity.offset() == START) {
				Optional<BotAbility<Message>> ability =
					abilityFactory.getMessageAbility(message.text().substring(START, entity.length()));
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
		MessageEntity[] messageEntities = message.entities();
		for (MessageEntity entity : messageEntities) {
			if (entity.type().equals(hashtag)) {
				int offset = entity.offset();
				Optional<BotAbility<Message>> ability =
					abilityFactory.getMessageAbility(message.text().substring(offset, offset + entity.length()));
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
		abilityFactory.getKeyboardAbility(callbackQuery.data()).ifPresent(keyboard ->
			keyboard.process(helper, sender, locale, callbackQuery));
	}

	private void sendCooldownAnswer(String callbackQueryId, long cooldownSec) {
		sender.sendCallbackQueryAnswer(callbackQueryId,
			String.format(locale.i18n("bot.inline.error.cooldown"), cooldownSec));
	}

	public void onNewUsers(Message message) {
		long chatId = message.chat().id();
		if (config.isShowGreetings() && service.checkGreeting(chatId)) {
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
			sender.replySimple(chatId, message.messageId(), botAnswer);
		}
	}

	public void onLeftUser(Message message) {
		long chatId = message.chat().id();
		if (config.isShowGreetings() && service.checkGreeting(chatId)) {
			String username = helper.getValidUsername(message.leftChatMember());
			if (!username.equals(telegram.getUsername())) {
				sender.replySimple(chatId, message.messageId(),
					locale.i18nRU("bot.event.user.left", username));
			}
		}
	}

	public void onNewPhotos(Message message) {
		long chatId = message.chat().id();
		if (config.isShowGreetings() && service.checkGreeting(chatId)) {
			sender.replySimple(chatId, message.messageId(), locale.i18n("bot.event.photo.change"));
		}
	}
}
