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

package ru.exlmoto.digest.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.MessageEntity.Type;
import com.pengrad.telegrambot.model.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.handler.BotHandler;
import ru.exlmoto.digest.bot.telegram.BotTelegram;

import javax.annotation.PostConstruct;

import java.util.List;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static com.pengrad.telegrambot.model.MessageEntity.Type.hashtag;

@Service
public class BotService implements UpdatesListener {
	private final Logger log = LoggerFactory.getLogger(BotService.class);

	private final BotConfiguration config;
	private final BotTelegram telegram;
	private final BotHandler handler;

	public BotService(BotConfiguration config, BotTelegram telegram, BotHandler handler) {
		this.config = config;
		this.telegram = telegram;
		this.handler = handler;
	}

	@PostConstruct
	private void setUp() {
		if (config.isInitialize()) {
			telegram.getBot().setUpdatesListener(this);
		}
	}

	@Override
	public int process(List<Update> updates) {
		if (!ObjectUtils.isEmpty(updates)) {
			int updatesCount = updates.size();
			int maxUpdates = config.getMaxUpdates();
			if (updatesCount > maxUpdates) {
				log.info(String.format("Too many '%d' updates received, shrink updates array to '%d'.",
					updatesCount, maxUpdates));
				updates.subList(0, updatesCount - maxUpdates).clear();
			}
			for (Update update : updates) {
				processUpdate(update);
			}
		}
		return CONFIRMED_UPDATES_ALL;
	}

	public void processUpdate(Update update) {
		try {
			if (config.isLogUpdates()) {
				log.info(update.toString());
			}
			Message message = checkForward(checkMessage(update));
			if (message != null) {
				processMessage(message);
			} else if (checkCallbackQuery(update)) {
				handler.onCallbackQuery(update.callbackQuery());
			}
		} catch (Exception e) {
			log.error("Unexpected exception on Bot Service.", e);
		}
	}

	private void processMessage(Message message) {
		if (checkCommand(message)) {
			handler.onCommand(message);
		} else if (checkHashTag(message)) {
			handler.onHashTag(message);
		} else if (checkNewUsers(message)) {
			handler.onNewUsers(message);
		} else if (checkLeftUser(message)) {
			handler.onLeftUser(message);
		} else if (checkNewPhotos(message)) {
			handler.onNewPhotos(message);
		}
	}

	private Message checkForward(Message message) {
		return (message != null && message.forwardDate() == null) ? message : null;
	}

	private Message checkMessage(Update update) {
		Message editedMessage = update.editedMessage();
		return (editedMessage != null) ? editedMessage : update.message();
	}

	private boolean checkCallbackQuery(Update update) {
		return update.callbackQuery() != null;
	}

	private boolean checkNewUsers(Message message) {
		return !ObjectUtils.isEmpty(message.newChatMembers());
	}

	private boolean checkLeftUser(Message message) {
		return message.leftChatMember() != null;
	}

	private boolean checkNewPhotos(Message message) {
		return !ObjectUtils.isEmpty(message.newChatPhoto());
	}

	private boolean checkCommand(Message message) {
		return checkEntities(message.entities(), bot_command);
	}

	private boolean checkHashTag(Message message) {
		return checkEntities(message.entities(), hashtag);
	}

	private boolean checkEntities(MessageEntity[] entities, Type entity) {
		if (entities != null) {
			for (MessageEntity messageEntity : entities) {
				if (messageEntity.type().equals(entity)) {
					return true;
				}
			}
		}
		return false;
	}
}
