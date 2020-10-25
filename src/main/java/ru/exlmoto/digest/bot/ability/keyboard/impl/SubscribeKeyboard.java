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

package ru.exlmoto.digest.bot.ability.keyboard.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Chat.Type;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.bot.ability.keyboard.Keyboard;
import ru.exlmoto.digest.bot.ability.keyboard.KeyboardSimpleAbility;
import ru.exlmoto.digest.bot.configuration.BotConfiguration;
import ru.exlmoto.digest.bot.sender.BotSender;
import ru.exlmoto.digest.bot.util.BotHelper;
import ru.exlmoto.digest.entity.BotSubCovidUaEntity;
import ru.exlmoto.digest.entity.BotSubDigestEntity;
import ru.exlmoto.digest.entity.BotSubMotofanEntity;
import ru.exlmoto.digest.entity.BotSubCovidEntity;
import ru.exlmoto.digest.service.DatabaseService;
import ru.exlmoto.digest.util.Answer;
import ru.exlmoto.digest.util.i18n.LocaleHelper;

import javax.annotation.PostConstruct;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class SubscribeKeyboard extends KeyboardSimpleAbility {
	private final Logger log = LoggerFactory.getLogger(SubscribeKeyboard.class);

	private enum Subscription {
		digest_subscribe,
		digest_unsubscribe,
		motofan_subscribe,
		motofan_unsubscribe,
		covid_subscribe,
		covid_unsubscribe,
		covid_ua_subscribe,
		covid_ua_unsubscribe
	}

	private enum Covid {
		ru,
		ua
	}

	private final BotHelper helper;
	private final LocaleHelper locale;
	private final DatabaseService service;
	private final BotConfiguration config;

	private InlineKeyboardMarkup markup = null;

	public SubscribeKeyboard(BotHelper helper,
	                         LocaleHelper locale,
	                         DatabaseService service,
	                         BotConfiguration config) {
		this.helper = helper;
		this.locale = locale;
		this.service = service;
		this.config = config;
	}

	@PostConstruct
	private void generateKeyboard() {
		markup = new InlineKeyboardMarkup(
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.motofan"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.motofan_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.motofan"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.motofan_unsubscribe)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.digest"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.digest_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.digest"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.digest_unsubscribe)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.covid"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.covid_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.covid"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.covid_unsubscribe)
			},
			new InlineKeyboardButton[] {
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.subscribe.covid.ua"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.covid_ua_subscribe),
				new InlineKeyboardButton(locale.i18n("bot.command.subscribe.button.unsubscribe.covid.ua"))
					.callbackData(Keyboard.subscribe.withName() + Subscription.covid_ua_unsubscribe)
			}
		);
	}

	@Override
	public InlineKeyboardMarkup getMarkup() {
		return markup;
	}

	@Override
	protected void execute(BotHelper helper, BotSender sender, LocaleHelper locale, CallbackQuery callback) {
		Message message = callback.message();
		int messageId = message.messageId();
		Chat chat = message.chat();

		String callbackId = callback.id();
		String key = Keyboard.chopKeyboardNameLeft(callback.data());

		if (!chat.type().equals(Type.Private)) {
			if (helper.isUserAdmin(callback.from().username())) {
				handleSubscription(messageId, chat, callbackId, sender, checkSubscription(key));
			} else {
				sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.admin"));
			}
		} else {
			handleSubscription(messageId, chat, callbackId, sender, checkSubscription(key));
		}
	}

	public void processSubscribeStatusMessage(int messageId, Chat chat, boolean edit, BotSender sender) {
		Answer<String> res = generateSubscribeStatusMessage(chat);
		String answer = res.ok() ? res.answer() : res.error();
		InlineKeyboardMarkup inlineKeyboardMarkup = res.ok() ? markup : null;
		if (edit) {
			sender.editMarkdown(chat.id(), messageId, answer, inlineKeyboardMarkup);
		} else {
			sender.replyMarkdown(chat.id(), messageId, answer, inlineKeyboardMarkup);
		}
	}

	private Answer<String> generateSubscribeStatusMessage(Chat chat) {
		long chatId = chat.id();
		String chatTitle = helper.getValidChatName(chat);
		try {
			return Ok(
				String.format(
					locale.i18n("bot.command.subscribe"), chatTitle, chatId,
					getSubscribeStatus(service.getMotofanSub(chatId) != null),
					getSubscribeStatus(service.getDigestSub(chatId) != null),
					getSubscribeStatus(service.getCovidSub(chatId) != null),
					getSubscribeStatus(service.getCovidUaSub(chatId) != null)
				)
			);
		} catch (DataAccessException dae) {
			log.error("Cannot get subscribe object from database.", dae);
			return Error(String.format(locale.i18n("bot.error.database"), dae.getLocalizedMessage()));
		}
	}

	private String getSubscribeStatus(boolean status) {
		return (status) ?
			locale.i18n("bot.command.subscribe.subscribed") :
			locale.i18n("bot.command.subscribe.unsubscribed");
	}

	private Subscription checkSubscription(String key) {
		try {
			return Subscription.valueOf(key);
		} catch (IllegalArgumentException iae) {
			log.error(String.format("Wrong subscribe key: '%s', return default '%s'.",
				key, Subscription.motofan_subscribe), iae);
		}
		return Subscription.motofan_subscribe;
	}

	private void handleSubscription(int messageId, Chat chat, String callbackId, BotSender sender, Subscription sub) {
		try {
			switch (sub) {
				case motofan_subscribe: {
					motofanSubscribe(messageId, chat, callbackId, sender);
					break;
				}
				case motofan_unsubscribe: {
					motofanUnsubscribe(messageId, chat, callbackId, sender);
					break;
				}
				case digest_subscribe: {
					digestSubscribe(messageId, chat, callbackId, sender);
					break;
				}
				case digest_unsubscribe: {
					digestUnsubscribe(messageId, chat, callbackId, sender);
					break;
				}
				case covid_subscribe: {
					covidSubscribe(messageId, chat, callbackId, sender, Covid.ru);
					break;
				}
				case covid_unsubscribe: {
					covidUnsubscribe(messageId, chat, callbackId, sender, Covid.ru);
					break;
				}
				case covid_ua_subscribe: {
					covidSubscribe(messageId, chat, callbackId, sender, Covid.ua);
					break;
				}
				case covid_ua_unsubscribe: {
					covidUnsubscribe(messageId, chat, callbackId, sender, Covid.ua);
					break;
				}
			}
		} catch (DataAccessException dae) {
			log.error("Cannot save or delete subscribe object from database.", dae);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.database"));
		}
	}

	private void motofanSubscribe(int messageId, Chat chat, String callbackId, BotSender sender) {
		long chatId = chat.id();
		if (service.getMotofanSub(chatId) != null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.exist"));
		} else {
			service.saveMotofanSub(new BotSubMotofanEntity(chatId, helper.getValidChatName(chat)));
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.subscribed"));
			processSubscribeStatusMessage(messageId, chat, true, sender);
		}
	}

	private void motofanUnsubscribe(int messageId, Chat chat, String callbackId, BotSender sender) {
		long chatId = chat.id();
		if (service.getMotofanSub(chatId) == null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.unsubscribe.exist"));
		} else {
			service.deleteMotofanSub(chatId);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.unsubscribed"));
			processSubscribeStatusMessage(messageId, chat, true, sender);
		}
	}

	private void digestSubscribe(int messageId, Chat chat, String callbackId, BotSender sender) {
		long chatId = chat.id();
		if (chatId != config.getMotofanChatId()) {
			if (service.getDigestSub(chatId) != null) {
				sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.exist"));
			} else {
				service.saveDigestSub(new BotSubDigestEntity(chatId, helper.getValidChatName(chat)));
				sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.subscribed"));
				processSubscribeStatusMessage(messageId, chat, true, sender);
			}
		} else {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.same"));
		}
	}

	private void digestUnsubscribe(int messageId, Chat chat, String callbackId, BotSender sender) {
		long chatId = chat.id();
		if (service.getDigestSub(chatId) == null) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.unsubscribe.exist"));
		} else {
			service.deleteDigestSub(chatId);
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.unsubscribed"));
			processSubscribeStatusMessage(messageId, chat, true, sender);
		}
	}

	private void covidSubscribe(int messageId, Chat chat, String callbackId, BotSender sender, Covid stat) {
		long chatId = chat.id();
		if (checkCovidSubscribe(chatId, stat)) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.subscribe.exist"));
		} else {
			switch (stat) {
				default:
				case ru: {
					service.saveCovidSub(new BotSubCovidEntity(chatId, helper.getValidChatName(chat)));
					break;
				}
				case ua: {
					service.saveCovidUaSub(new BotSubCovidUaEntity(chatId, helper.getValidChatName(chat)));
					break;
				}
			}
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.subscribed"));
			processSubscribeStatusMessage(messageId, chat, true, sender);
		}
	}

	private void covidUnsubscribe(int messageId, Chat chat, String callbackId, BotSender sender, Covid stat) {
		long chatId = chat.id();
		if (!checkCovidSubscribe(chatId, stat)) {
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.error.unsubscribe.exist"));
		} else {
			switch (stat) {
				default:
				case ru: {
					service.deleteCovidSub(chatId);
					break;
				}
				case ua: {
					service.deleteCovidUaSub(chatId);
					break;
				}
			}
			sender.sendCallbackQueryAnswer(callbackId, locale.i18n("bot.inline.subscribe.unsubscribed"));
			processSubscribeStatusMessage(messageId, chat, true, sender);
		}
	}

	private boolean checkCovidSubscribe(long chatId, Covid stat) {
		switch (stat) {
			default:
			case ru: {
				return service.getCovidSub(chatId) != null;
			}
			case ua: {
				return service.getCovidUaSub(chatId) != null;
			}
		}
	}
}
